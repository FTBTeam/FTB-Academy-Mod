package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbquests.block.FTBQuestsBlocks;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.QuestTask;
import com.feed_the_beast.ftbquests.tile.TileProgressDetector;
import com.feed_the_beast.ftbquests.tile.TileTaskScreenCore;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockManaDetector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBAcademyMod.MOD_ID)
public class EventHandlerFTBAM
{
	private static final Collection<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList("quit_school", "reset_school", "w", "msg", "tell"));
	private static Template template = null;
	private static BlockPos spawn = new BlockPos(0, 0, 0);
	private static EnumFacing spawnFacing = EnumFacing.NORTH;
	private static Map<BlockPos, SpecialBlockPlacement> special = new HashMap<>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockManaDetector().setRegistryName("mana_detector"));

		GameRegistry.registerTileEntity(BlockManaDetector.Entity.class, new ResourceLocation(FTBAcademyMod.MOD_ID, "mana_detector"));
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		EntityPlayerMP playerMP = event.getPlayer().getPlayer();

		if (FTBAcademyMod.getTutorialPhase(playerMP) == 0)
		{
			teleportToIsland(playerMP);
		}
	}

	public static void teleportToIsland(EntityPlayerMP playerMP)
	{
		if (template == null)
		{
			template = new Template();

			File file = new File(Loader.instance().getConfigDir(), "school.nbt");

			if (file.exists() && file.isFile())
			{
				try (FileInputStream fis = new FileInputStream(file))
				{
					template.read(CompressedStreamTools.readCompressed(fis));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			for (Map.Entry<BlockPos, String> entry : template.getDataBlocks(BlockPos.ORIGIN, new PlacementSettings()).entrySet())
			{
				Map<String, String> map = new HashMap<>();

				for (String s : entry.getValue().split(","))
				{
					String[] s1 = s.split("=", 2);

					if (s1.length == 2)
					{
						map.put(s1[0], s1[1]);
					}
				}

				switch (map.getOrDefault("type", ""))
				{
					case "":
						break;
					case "spawn_point":
						spawn = entry.getKey();
						spawnFacing = EnumFacing.byName(map.getOrDefault("facing", "north"));
						break;
					case "detector":
						special.put(entry.getKey(), (world, pos, player) -> {
							QuestObject object = ServerQuestFile.INSTANCE.get(ServerQuestFile.INSTANCE.getID(map.getOrDefault("id", "")));
							String team = FTBLibAPI.getTeam(player.getUniqueID());

							if (object != null && !team.isEmpty())
							{
								world.setBlockState(pos, FTBQuestsBlocks.PROGRESS_DETECTOR.getDefaultState(), 3);
								TileEntity tileEntity = world.getTileEntity(pos);

								if (tileEntity instanceof TileProgressDetector)
								{
									((TileProgressDetector) tileEntity).team = team;
									((TileProgressDetector) tileEntity).object = object.id;
								}
							}
						});
						break;
					case "task_screen":
						special.put(entry.getKey(), (world, pos, player) -> {
							QuestTask task = ServerQuestFile.INSTANCE.getTask(ServerQuestFile.INSTANCE.getID(map.getOrDefault("id", "")));
							String team = FTBLibAPI.getTeam(player.getUniqueID());

							if (task != null && !team.isEmpty())
							{
								EnumFacing facing = EnumFacing.byName(map.getOrDefault("facing", "north"));
								world.setBlockState(pos, FTBQuestsBlocks.SCREEN.getDefaultState().withProperty(BlockHorizontal.FACING, facing), 3);
								TileEntity tileEntity = world.getTileEntity(pos);

								if (tileEntity instanceof TileTaskScreenCore)
								{
									TileTaskScreenCore screen = (TileTaskScreenCore) tileEntity;
									screen.team = team;
									screen.quest = task.quest.id;
									screen.task = task.id;
								}
							}
						});
						break;
					case "mana_detector":
						special.put(entry.getKey(), (world, pos, player) -> {
							world.setBlockState(pos, FTBAcademyMod.MANA_DETECTOR.getDefaultState(), 3);
							TileEntity tileEntity = world.getTileEntity(pos);

							if (tileEntity instanceof BlockManaDetector.Entity)
							{
								BlockManaDetector.Entity manaDetector = (BlockManaDetector.Entity) tileEntity;
								manaDetector.id = ServerQuestFile.INSTANCE.getID(map.getOrDefault("id", ""));
								manaDetector.player = player.getUniqueID();
								manaDetector.distance = Integer.parseInt(map.getOrDefault("dist", "2"));
							}
						});
						break;
				}
			}
		}

		World world = playerMP.server.getWorld(ConfigFTBAM.general.dimension_id);

		if (!(world.provider instanceof WorldProviderFTBAM))
		{
			return;
		}

		WorldProviderFTBAM provider = (WorldProviderFTBAM) world.provider;

		int rx = provider.schoolsSpawned % 100;
		int rz = provider.schoolsSpawned / 100;

		BlockPos pos = new BlockPos(256 + rx * 512, 70, 256 + rz * 512);
		template.addBlocksToWorld(world, pos, new PlacementSettings(), 2);
		world.getPendingBlockUpdates(new StructureBoundingBox(pos, pos.add(template.getSize())), true);

		for (Map.Entry<BlockPos, SpecialBlockPlacement> entry : special.entrySet())
		{
			entry.getValue().place(world, pos.add(entry.getKey()), playerMP);
		}

		playerMP.inventory.clear();
		TeleporterDimPos.of(pos.add(spawn), world.provider.getDimension()).teleport(playerMP);
		playerMP.connection.setPlayerLocation(playerMP.posX, playerMP.posY, playerMP.posZ, spawnFacing.getHorizontalAngle(), 0F);
		playerMP.setSpawnPoint(pos.add(spawn), true);
		FTBAcademyMod.setTutorialPhase(playerMP, 1);
		playerMP.setGameType(GameType.ADVENTURE);
		provider.schoolsSpawned++;
	}

	@SubscribeEvent
	public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
	{
		FTBAcademyMod.setTutorialPhase(event.getEntityPlayer(), FTBAcademyMod.getTutorialPhase(event.getOriginal()));
	}

	@SubscribeEvent
	public static void onCommand(CommandEvent event)
	{
		if (event.getSender() instanceof EntityPlayerMP && FTBAcademyMod.getTutorialPhase((EntityPlayerMP) event.getSender()) == 1 && !ALLOWED_COMMANDS.contains(event.getCommand().getName()))
		{
			event.setException(new CommandException("ftbacademymod.command_error"));
			event.setCanceled(true);
		}
	}
}