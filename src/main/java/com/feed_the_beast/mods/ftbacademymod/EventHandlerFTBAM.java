package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamCreatedEvent;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbquests.item.FTBQuestsItems;
import com.feed_the_beast.ftbquests.quest.ChangeProgress;
import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.util.ServerQuestData;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDetector;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDetectorEntity;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialBlockPlacement;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialQuestDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialTaskScreen;
import com.feed_the_beast.mods.ftbacademymod.util.Interaction;
import com.feed_the_beast.mods.ftbacademymod.util.LeftClick;
import dev.latvian.kubejs.block.BlockRightClickEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
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
	private static final HashMap<BlockPos, SpecialBlockPlacement> special = new HashMap<>();
	private static final HashSet<String> allowedCommands = new HashSet<>();
	private static final HashSet<ResourceLocation> revokedAdvancements = new HashSet<>();

	static
	{
		allowedCommands.add("quit_school");
		allowedCommands.add("reset_school");
		allowedCommands.add("w");
		allowedCommands.add("msg");
		allowedCommands.add("tell");
		allowedCommands.add("gamemode");
		allowedCommands.add("ftbquests");
		allowedCommands.add("nbtedit");
		allowedCommands.add("kick");
		allowedCommands.add("op");
		allowedCommands.add("deop");
		allowedCommands.add("ban");
		allowedCommands.add("stop");
		allowedCommands.add("help");
		allowedCommands.add("gamestage");
		allowedCommands.add("kubejs");
		allowedCommands.add("give");

		revokedAdvancements.add(new ResourceLocation("astralsorcery:root"));
		revokedAdvancements.add(new ResourceLocation("minecraft:story/root"));
		revokedAdvancements.add(new ResourceLocation("minecraft:story/mine_stone"));
		revokedAdvancements.add(new ResourceLocation("minecraft:story/smelt_iron"));
		revokedAdvancements.add(new ResourceLocation("refinedstorage:storing_items"));
		revokedAdvancements.add(new ResourceLocation("botania:main/root"));
		revokedAdvancements.add(new ResourceLocation("botania:main/flower_pickup"));
		revokedAdvancements.add(new ResourceLocation("botania:main/generating_flower"));
		revokedAdvancements.add(new ResourceLocation("botania:challenge/root"));
	}

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

		IStageData stageData = GameStageHelper.getPlayerData(p);

		if (restart)
		{
			ServerQuestFile.INSTANCE.changeProgress(ServerQuestFile.INSTANCE.getData(p), ChangeProgress.RESET);
			p.inventory.clear();

			for (String s : new HashSet<>(stageData.getStages()))
			{
				stageData.removeStage(s);
			}
		}

		ItemHandlerHelper.giveItemToPlayer(p, new ItemStack(FTBQuestsItems.BOOK));
		stageData.addStage("ftba_welcome_to_academy");
		stageData.addStage("in_first_room");

		GameStageHelper.syncPlayer(p);

		FTBAcademyMod.setSchoolPhase(p, 1);
		provider.schoolsSpawned++;
	}

	public static void completeSchoolQuests(EntityPlayerMP p)
	{
		ServerQuestData data = (ServerQuestData) ServerQuestFile.INSTANCE.getData(p);

		if (data == null)
		{
			return;
		}

		Chapter chapter = ServerQuestFile.INSTANCE.getChapter(0x6f61040f);
		Chapter backend = ServerQuestFile.INSTANCE.getChapter(0x53fecf41);

		if (chapter == null || backend == null)
		{
			return;
		}

		for (Quest quest : chapter.quests)
		{
			for (Reward reward : quest.rewards)
			{
				data.setRewardClaimed(p.getUniqueID(), reward);
			}
		}

		for (Quest quest : backend.quests)
		{
			for (Reward reward : quest.rewards)
			{
				data.setRewardClaimed(p.getUniqueID(), reward);
			}
		}

		chapter.forceProgress(data, ChangeProgress.COMPLETE_DEPS, false);
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

		p.inventory.clear();
		ItemHandlerHelper.giveItemToPlayer(p, new ItemStack(FTBQuestsItems.BOOK));

		ItemStack guideBook = new ItemStack(Items.BOOK);
		guideBook.setTagInfo("guide", new NBTTagString(""));
		guideBook.setTranslatableName("item.ftbguides.book.name");
		ItemHandlerHelper.giveItemToPlayer(p, guideBook);

		for (ResourceLocation id : revokedAdvancements)
		{
			Advancement advancement = p.server.getAdvancementManager().getAdvancement(id);

			if (advancement != null)
			{
				AdvancementProgress advancementprogress = p.getAdvancements().getProgress(advancement);

				if (advancementprogress.hasProgress())
				{
					for (String s : advancementprogress.getCompletedCriteria())
					{
						p.getAdvancements().revokeCriterion(advancement, s);
					}
				}
			}
		}

		p.server.getCommandManager().executeCommand(p.server, "/as reset " + p.getName());
		ITextComponent gradName = p.getDisplayName().createCopy();
		gradName.getStyle().setColor(TextFormatting.DARK_AQUA);
		p.server.getPlayerList().sendMessage(new TextComponentTranslation("ftbacademymod.graduated", gradName));

		IStageData stageData = GameStageHelper.getPlayerData(p);

		for (String s : new HashSet<>(stageData.getStages()))
		{
			stageData.removeStage(s);
		}

		GameStageHelper.syncPlayer(p);

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
		ForgePlayer o = event.getTeam().getOwner();

		if (o != null && o.isOnline() && FTBAcademyMod.getSchoolPhase(o.getPlayer()) == 2)
		{
			completeSchoolQuests(o.getPlayer());
		}
	}

	@SubscribeEvent
	public static void onCommand(CommandEvent event)
	{
		if (event.getSender() instanceof EntityPlayerMP && FTBAcademyMod.isInSchool((EntityPlayerMP) event.getSender()) && !allowedCommands.contains(event.getCommand().getName()))
		{
			event.getSender().sendMessage(new TextComponentTranslation("ftbacademymod.command_error"));
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onContainerClosed(PlayerContainerEvent.Close event)
	{
		if (event.getContainer() instanceof ContainerChest && FTBAcademyMod.isInSchool(event.getEntityPlayer()) && GameStageHelper.hasAnyOf(event.getEntityPlayer(), "a1", "b_petal"))
		{
			IInventory inventory = ((ContainerChest) event.getContainer()).getLowerChestInventory();

			if (inventory instanceof TileEntityChest && inventory.isEmpty())
			{
				event.getEntityPlayer().world.setBlockToAir(((TileEntityChest) inventory).getPos());
			}
		}
	}

	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event)
	{
		if (event.player instanceof EntityPlayerMP && event.player.ticksExisted % 20 == 0 && FTBAcademyMod.isInSchool(event.player))
		{
			event.player.getFoodStats().addStats(20, 1F);
		}
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if (FTBAcademyMod.isInSchool(event.getPlayer()))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if (!FTBAcademyMod.isInSchool(event.getEntityPlayer()))
		{
			return;
		}

		if (event.getHand() == EnumHand.OFF_HAND)
		{
			event.setCanceled(true);
			return;
		}

		BlockRightClickEventJS e = new BlockRightClickEventJS(event);
		Interaction i = Interaction.find(e);

		if (i != null)
		{
			if (i.action != null && !i.action.onEvent(e))
			{
				event.setCanceled(true);
			}
		}
		else
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		if (FTBAcademyMod.isInSchool(event.getEntityPlayer()) && !LeftClick.canLeftClick(new BlockContainerJS(event.getWorld(), event.getPos())))
		{
			event.setCanceled(true);
		}
	}

	@GameRegistry.ObjectHolder("botania:petal")
	public static Item PETAL_ITEM;

	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		if (FTBAcademyMod.isInSchool(event.getPlayer()) && event.getPlayer().getHeldItem(event.getHand()).getItem() == PETAL_ITEM)
		{
			event.setCanceled(true);
		}
	}
}