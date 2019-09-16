package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamCreatedEvent;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbquests.quest.ChangeProgress;
import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.util.ServerQuestData;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDetector;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDetectorEntity;
import com.feed_the_beast.mods.ftbacademymod.kubejs.KubeJSIntegration;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialBlockPlacement;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialQuestDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialTaskScreen;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBAcademyMod.MOD_ID)
public class EventHandlerFTBAM
{
	private static Template template = null;
	private static BlockPos spawn = new BlockPos(0, 0, 0);
	private static EnumFacing spawnFacing = EnumFacing.NORTH;
	private static HashMap<BlockPos, SpecialBlockPlacement> special = new HashMap<>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockDetector().setRegistryName("detector"));
		GameRegistry.registerTileEntity(BlockDetectorEntity.class, new ResourceLocation(FTBAcademyMod.MOD_ID, "detector"));
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		EntityPlayerMP playerMP = event.getPlayer().getPlayer();

		int p = FTBAcademyMod.getSchoolPhase(playerMP);

		if (p == 0)
		{
			teleportToSchool(playerMP, false);
		}
		else if (p == 2)
		{
			completeSchoolQuests(playerMP);
		}
	}

	public static void teleportToSchool(EntityPlayerMP p, boolean restart)
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
					{
						String type = map.getOrDefault("detector", "");
						map.remove("detector");

						if (!type.isEmpty())
						{
							special.put(entry.getKey(), new SpecialDetector(type, map));
						}
					}
					break;
					case "spawn_point":
						spawn = entry.getKey();
						spawnFacing = EnumFacing.byName(map.getOrDefault("facing", "north"));
						break;
					case "quest_detector":
						special.put(entry.getKey(), new SpecialQuestDetector(ServerQuestFile.INSTANCE.getID(map.getOrDefault("id", ""))));
						break;
					case "task_screen":
						special.put(entry.getKey(), new SpecialTaskScreen(ServerQuestFile.INSTANCE.getID(map.getOrDefault("id", "")), EnumFacing.byName(map.getOrDefault("facing", "north"))));
						break;
				}
			}
		}

		World world = p.server.getWorld(ConfigFTBAM.general.dimension_id);

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
			entry.getValue().place(world, pos.add(entry.getKey()), p);
		}

		TeleporterDimPos.of(pos.add(spawn), world.provider.getDimension()).teleport(p);
		p.connection.setPlayerLocation(p.posX, p.posY, p.posZ, spawnFacing.getHorizontalAngle(), 0F);
		p.setSpawnPoint(pos.add(spawn), true);
		KubeJSIntegration.schoolStarted(p, restart);
		FTBAcademyMod.setSchoolPhase(p, 1);
		provider.schoolsSpawned++;
	}

	public static void completeSchoolQuests(EntityPlayerMP p)
	{
		Chapter chapter = ServerQuestFile.INSTANCE.getChapter(0x6f61040f);

		if (chapter != null)
		{
			ServerQuestData data = (ServerQuestData) ServerQuestFile.INSTANCE.getData(p);

			if (data != null)
			{
				for (Quest quest : chapter.quests)
				{
					for (Reward reward : quest.rewards)
					{
						data.setRewardClaimed(p.getUniqueID(), reward);
					}
				}

				chapter.forceProgress(data, ChangeProgress.COMPLETE, false);
			}
		}
	}

	public static void finishSchool(EntityPlayerMP p)
	{
		World world = p.server.getWorld(0);

		BlockPos spawnpoint = world.getSpawnPoint();

		while (world.getBlockState(spawnpoint).isFullCube())
		{
			spawnpoint = spawnpoint.up(2);
		}

		completeSchoolQuests(p);
		KubeJSIntegration.schoolEnded(p);
		p.inventoryContainer.detectAndSendChanges();
		TeleporterDimPos.of(spawnpoint, world.provider.getDimension()).teleport(p);
		p.setSpawnPoint(spawnpoint, false);
		//p.setGameType(GameType.SURVIVAL);
		FTBAcademyMod.setSchoolPhase(p, 2);

		if (p.server.getPlayerList().getPlayers().size() == 1)
		{
			for (int i = 0; i < p.server.worlds.length; ++i)
			{
				WorldServer worldserver = p.server.worlds[i];
				worldserver.setWorldTime(worldserver.getWorldTime() + (24000L - worldserver.getWorldTime() % 24000L) % 24000L);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onTeamCreated(ForgeTeamCreatedEvent event)
	{
		if (event.getTeam().hasOwner() && event.getTeam().owner.isOnline() && FTBAcademyMod.getSchoolPhase(event.getTeam().owner.entityPlayer) == 2)
		{
			completeSchoolQuests(event.getTeam().owner.entityPlayer);
		}
	}
}