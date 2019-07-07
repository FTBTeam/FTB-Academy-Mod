package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbquests.item.FTBQuestsItems;
import com.feed_the_beast.ftbquests.quest.ChangeProgress;
import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestFile;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.util.ServerQuestData;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDetector;
import com.feed_the_beast.mods.ftbacademymod.blocks.EnumDetectorType;
import com.feed_the_beast.mods.ftbacademymod.blocks.FilterItem;
import com.feed_the_beast.mods.ftbacademymod.net.MessageSyncPhase;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialBlockPlacement;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialQuestDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialTaskScreen;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
	private static final Collection<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList(
			"quit_school",
			"reset_school",
			"w",
			"msg",
			"tell",
			"gamemode",
			"ftbquests",
			"nbtedit",
			"kick",
			"op",
			"deop",
			"ban",
			"stop",
			"help"
	));

	private static Template template = null;
	private static BlockPos spawn = new BlockPos(0, 0, 0);
	private static EnumFacing spawnFacing = EnumFacing.NORTH;
	private static HashMap<BlockPos, SpecialBlockPlacement> special = new HashMap<>();
	private static HashMap<FilterItem, String> canPlaceOn = new HashMap<>();

	static
	{
		canPlaceOn.put(new FilterItem("botania:specialflower"), "minecraft:grass");
		canPlaceOn.put(new FilterItem("astralsorcery:blockaltar"), "minecraft:quartz_block");
		canPlaceOn.put(new FilterItem("thermaldynamics:duct_0"), "minecraft:wool");
	}

	public static final String[] REVOKE_ADVANCEMENTS = {
			"astralsorcery:root",
			"minecraft:story/root",
			"minecraft:story/mine_stone",
			"minecraft:story/smelt_iron",
			"refinedstorage:storing_items",
			"botania:main/root",
			"botania:main/flower_pickup",
			"botania:main/generating_flower",
			"botania:challenge/root",
	};

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockDetector().setRegistryName("detector"));

		for (EnumDetectorType type : EnumDetectorType.VALUES)
		{
			GameRegistry.registerTileEntity(type.clazz, new ResourceLocation(FTBAcademyMod.MOD_ID, "detector_" + type.name));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		EntityPlayerMP playerMP = event.getPlayer().getPlayer();
		int p = FTBAcademyMod.getTutorialPhase(playerMP);
		new MessageSyncPhase(p).sendTo(playerMP);

		if (p == 0)
		{
			teleportToSchool(playerMP);
		}
	}

	public static void teleportToSchool(EntityPlayerMP p)
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
						EnumDetectorType type = EnumDetectorType.NAME_MAP.get(map.getOrDefault("detector", ""));

						if (type != null)
						{
							map.remove("detector");
							special.put(entry.getKey(), new SpecialDetector(type, map));
						}
					}
					break;
					case "spawn_point":
						spawn = entry.getKey();
						spawnFacing = EnumFacing.byName(map.getOrDefault("facing", "north"));
						break;
					case "quest_detector":
						special.put(entry.getKey(), new SpecialQuestDetector(QuestFile.getID(map.getOrDefault("id", ""))));
						break;
					case "task_screen":
						special.put(entry.getKey(), new SpecialTaskScreen(QuestFile.getID(map.getOrDefault("id", "")), EnumFacing.byName(map.getOrDefault("facing", "north"))));
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

		QuestData data = ServerQuestFile.INSTANCE.getData(p);

		if (data != null)
		{
			ServerQuestFile.INSTANCE.forceProgress(data, ChangeProgress.RESET, false);
		}

		p.inventory.clear();
		p.inventory.addItemStackToInventory(new ItemStack(FTBQuestsItems.BOOK));
		ItemStack guideBook = new ItemStack(Items.BOOK);
		guideBook.setTagInfo("guide", new NBTTagString(""));
		guideBook.setTranslatableName("item.ftbguides.book.name");
		p.inventory.addItemStackToInventory(guideBook);
		TeleporterDimPos.of(pos.add(spawn), world.provider.getDimension()).teleport(p);
		p.connection.setPlayerLocation(p.posX, p.posY, p.posZ, spawnFacing.getHorizontalAngle(), 0F);
		p.setSpawnPoint(pos.add(spawn), true);
		p.setGameType(GameType.ADVENTURE);
		FTBAcademyMod.setTutorialPhase(p, 1);
		provider.schoolsSpawned++;
	}

	public static void finishSchool(EntityPlayerMP p)
	{
		World world = p.server.getWorld(0);

		BlockPos spawnpoint = world.getSpawnPoint();

		while (world.getBlockState(spawnpoint).isFullCube())
		{
			spawnpoint = spawnpoint.up(2);
		}

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

		p.inventory.clear();
		p.inventory.addItemStackToInventory(new ItemStack(FTBQuestsItems.BOOK));
		ItemStack guideBook = new ItemStack(Items.BOOK);
		guideBook.setTagInfo("guide", new NBTTagString(""));
		guideBook.setTranslatableName("item.ftbguides.book.name");
		p.inventory.addItemStackToInventory(guideBook);

		for (String s : REVOKE_ADVANCEMENTS)
		{
			Advancement a = p.server.getAdvancementManager().getAdvancement(new ResourceLocation(s));

			if (a != null)
			{
				AdvancementProgress advancementprogress = p.getAdvancements().getProgress(a);

				if (advancementprogress.hasProgress())
				{
					for (String s1 : advancementprogress.getCompletedCriteria())
					{
						p.getAdvancements().revokeCriterion(a, s1);
					}
				}
			}
		}

		p.server.getCommandManager().executeCommand(p.server, "as reset " + p.getName());

		ITextComponent name = p.getDisplayName();
		name.getStyle().setColor(TextFormatting.DARK_AQUA);
		p.server.getPlayerList().sendMessage(new TextComponentTranslation("ftbacademymod.graduated", name));

		p.inventoryContainer.detectAndSendChanges();
		TeleporterDimPos.of(spawnpoint, world.provider.getDimension()).teleport(p);
		p.setSpawnPoint(spawnpoint, false);
		p.setGameType(GameType.SURVIVAL);
		FTBAcademyMod.setTutorialPhase(p, 2);

		if (p.server.getPlayerList().getPlayers().size() == 1)
		{
			for (int i = 0; i < p.server.worlds.length; ++i)
			{
				WorldServer worldserver = p.server.worlds[i];
				worldserver.setWorldTime(worldserver.getWorldTime() + (24000L - worldserver.getWorldTime() % 24000L) % 24000L);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
	{
		FTBAcademyMod.setTutorialPhase(event.getEntityPlayer(), FTBAcademyMod.getTutorialPhase(event.getOriginal()));
	}

	@SubscribeEvent
	public static void onCommand(CommandEvent event)
	{
		if (event.getSender() instanceof EntityPlayerMP && FTBAcademyMod.isInTutorial((EntityPlayerMP) event.getSender()) && !ALLOWED_COMMANDS.contains(event.getCommand().getName()))
		{
			event.setException(new CommandException("ftbacademymod.command_error"));
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !event.player.world.isRemote && FTBAcademyMod.isInTutorial(event.player))
		{
			event.player.getFoodStats().addStats(20, 1F);
			boolean changed = false;

			for (int i = 0; i < 9; i++)
			{
				ItemStack stack = event.player.inventory.mainInventory.get(i);

				if (!stack.isEmpty() && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("CanPlaceOn")))
				{
					String s = canPlaceOn.get(new FilterItem(stack.getItem().getRegistryName().toString(), stack.getMetadata()));

					if (s != null)
					{
						NBTTagList list = new NBTTagList();
						list.appendTag(new NBTTagString(s));
						stack.setTagInfo("CanPlaceOn", list);
						changed = true;
					}
				}
			}

			if (changed)
			{
				event.player.inventoryContainer.detectAndSendChanges();
			}
		}
	}

	@SubscribeEvent
	public static void onItemRightClick(PlayerInteractEvent.RightClickItem event)
	{
		if (FTBAcademyMod.isInTutorial(event.getEntityPlayer()) && event.getItemStack().getItem() == Items.ENDER_PEARL)
		{
			event.setCanceled(true);
		}
	}
}