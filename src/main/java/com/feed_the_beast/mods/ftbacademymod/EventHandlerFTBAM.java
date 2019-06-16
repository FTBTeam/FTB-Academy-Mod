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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
	private static final Collection<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList("quit_school", "w", "msg", "tell"));
	private static Template template = null;
	private static BlockPos spawn = null;
	private static HashMap<BlockPos, SpecialBlockPlacement> special = null;

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		EntityPlayerMP playerMP = event.getPlayer().getPlayer();

		if (FTBAcademyMod.getTutorialPhase(playerMP) != 0)
		{
			return;
		}

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

			spawn = new BlockPos(0, 1, 0);
			special = new HashMap<>();

			for (Map.Entry<BlockPos, String> entry : template.getDataBlocks(BlockPos.ORIGIN, new PlacementSettings()).entrySet())
			{
				if (entry.getValue().equals("SPAWN_POINT"))
				{
					spawn = entry.getKey();
				}
				else if (entry.getValue().startsWith("DETECTOR="))
				{
					int id = ServerQuestFile.INSTANCE.getID(entry.getValue().substring(9));

					special.put(entry.getKey(), (world, pos, player) -> {
						QuestObject object = ServerQuestFile.INSTANCE.get(id);
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
				}
				else if (entry.getValue().startsWith("TASK_SCREEN="))
				{
					int id = ServerQuestFile.INSTANCE.getID(entry.getValue().substring(9));

					special.put(entry.getKey(), (world, pos, player) -> {
						QuestTask task = ServerQuestFile.INSTANCE.getTask(id);
						String team = FTBLibAPI.getTeam(player.getUniqueID());

						if (task != null && !team.isEmpty())
						{
							world.setBlockState(pos, FTBQuestsBlocks.SCREEN.getDefaultState(), 3);
							TileEntity tileEntity = world.getTileEntity(pos);

							if (tileEntity instanceof TileTaskScreenCore)
							{
								((TileTaskScreenCore) tileEntity).team = team;
								((TileTaskScreenCore) tileEntity).quest = task.quest.id;
								((TileTaskScreenCore) tileEntity).task = task.id;
							}
						}
					});
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

		TeleporterDimPos.of(pos.add(spawn), world.provider.getDimension()).teleport(playerMP);
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
			event.setCanceled(true);
		}
	}
}