package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbquests.item.FTBQuestsItems;
import com.feed_the_beast.ftbquests.net.MessageDisplayRewardToast;
import com.feed_the_beast.ftbquests.net.edit.MessageChangeProgressResponse;
import com.feed_the_beast.ftbquests.quest.EnumChangeProgress;
import com.feed_the_beast.ftbquests.quest.ITeamData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestChapter;
import com.feed_the_beast.ftbquests.quest.QuestFile;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.reward.QuestReward;
import com.feed_the_beast.ftbquests.util.FTBQuestsTeamData;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDuctDetector;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockManaDetector;
import com.feed_the_beast.mods.ftbacademymod.net.MessageSyncPhase;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialBlockPlacement;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialDuctDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialManaDetector;
import com.feed_the_beast.mods.ftbacademymod.special.SpecialTaskScreen;
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
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
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
	private static final Collection<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList("quit_school", "reset_school", "w", "msg", "tell", "ftbquests", "nbtedit", "kick", "op", "deop", "ban", "stop", "help"));
	private static Template template = null;
	private static BlockPos spawn = new BlockPos(0, 0, 0);
	private static EnumFacing spawnFacing = EnumFacing.NORTH;
	private static Map<BlockPos, SpecialBlockPlacement> special = new HashMap<>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockManaDetector().setRegistryName("mana_detector"));
		event.getRegistry().register(new BlockDuctDetector().setRegistryName("duct_detector"));

		GameRegistry.registerTileEntity(BlockManaDetector.Entity.class, new ResourceLocation(FTBAcademyMod.MOD_ID, "mana_detector"));
		GameRegistry.registerTileEntity(BlockDuctDetector.Entity.class, new ResourceLocation(FTBAcademyMod.MOD_ID, "duct_detector"));
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
						break;
					case "spawn_point":
						spawn = entry.getKey();
						spawnFacing = EnumFacing.byName(map.getOrDefault("facing", "north"));
						break;
					case "detector":
						special.put(entry.getKey(), new SpecialDetector(QuestFile.getID(map.getOrDefault("id", ""))));
						break;
					case "task_screen":
						special.put(entry.getKey(), new SpecialTaskScreen(QuestFile.getID(map.getOrDefault("id", "")), EnumFacing.byName(map.getOrDefault("facing", "north"))));
						break;
					case "mana_detector":
						special.put(entry.getKey(), new SpecialManaDetector(QuestFile.getID(map.getOrDefault("id", "")), Integer.parseInt(map.getOrDefault("dist", "2"))));
						break;
					case "duct_detector":
						special.put(entry.getKey(), new SpecialDuctDetector(QuestFile.getID(map.getOrDefault("id", "")), Integer.parseInt(map.getOrDefault("dist", "2")), map.getOrDefault("variant", "")));
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

		ITeamData data = ServerQuestFile.INSTANCE.getData(p);

		if (data != null)
		{
			EnumChangeProgress.sendUpdates = false;
			ServerQuestFile.INSTANCE.changeProgress(data, EnumChangeProgress.RESET);
			EnumChangeProgress.sendUpdates = true;
			new MessageChangeProgressResponse(data.getTeamUID(), ServerQuestFile.INSTANCE.id, EnumChangeProgress.RESET).sendToAll();
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

		QuestChapter chapter = ServerQuestFile.INSTANCE.getChapter(0x6f61040f);

		if (chapter != null)
		{
			FTBQuestsTeamData data = (FTBQuestsTeamData) ServerQuestFile.INSTANCE.getData(p);

			if (data != null)
			{
				EnumChangeProgress.sendUpdates = false;
				MessageDisplayRewardToast.ENABLED = false;
				chapter.changeProgress(data, EnumChangeProgress.COMPLETE);

				for (Quest quest : chapter.quests)
				{
					for (QuestReward reward : quest.rewards)
					{
						data.claimReward(p, reward, false);
					}
				}

				MessageDisplayRewardToast.ENABLED = true;
				EnumChangeProgress.sendUpdates = true;
				new MessageChangeProgressResponse(data.getTeamUID(), chapter.id, EnumChangeProgress.COMPLETE).sendToAll();
			}
		}

		p.inventory.clear();
		p.inventory.addItemStackToInventory(new ItemStack(FTBQuestsItems.BOOK));
		ItemStack guideBook = new ItemStack(Items.BOOK);
		guideBook.setTagInfo("guide", new NBTTagString(""));
		guideBook.setTranslatableName("item.ftbguides.book.name");
		p.inventory.addItemStackToInventory(guideBook);

		p.server.getCommandManager().executeCommand(p.server, "advancement revoke " + p.getName() + " everything");
		p.server.getCommandManager().executeCommand(p.server, "as reset " + p.getName());

		ITextComponent name = p.getDisplayName();
		name.getStyle().setColor(TextFormatting.DARK_AQUA);
		p.server.getPlayerList().sendMessage(new TextComponentTranslation("ftbacademymod.graduated", name));

		p.inventoryContainer.detectAndSendChanges();
		TeleporterDimPos.of(spawnpoint, world.provider.getDimension()).teleport(p);
		p.setSpawnPoint(spawnpoint, false);
		p.setGameType(GameType.SURVIVAL);
		FTBAcademyMod.setTutorialPhase(p, 2);
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
			boolean changed = false;

			for (int i = 0; i < 9; i++)
			{
				ItemStack stack = event.player.inventory.mainInventory.get(i);

				if (stack.getItem() == ItemsFTBAM.FLOWER)
				{
					NBTTagList list = new NBTTagList();
					list.appendTag(new NBTTagString("minecraft:grass"));
					stack.setTagInfo("CanPlaceOn", list);
					changed = true;
				}
				else if (stack.getItem() == ItemsFTBAM.ALTAR)
				{
					NBTTagList list = new NBTTagList();
					list.appendTag(new NBTTagString("minecraft:quartz_block"));
					stack.setTagInfo("CanPlaceOn", list);
					changed = true;
				}
			}

			if (changed)
			{
				event.player.inventoryContainer.detectAndSendChanges();
			}
		}
	}
}