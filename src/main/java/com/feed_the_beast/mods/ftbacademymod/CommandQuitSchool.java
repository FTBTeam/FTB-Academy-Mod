package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbquests.item.FTBQuestsItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class CommandQuitSchool extends CommandBase
{
	@Override
	public String getName()
	{
		return "quit_school";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.quit_school.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = args.length > 0 && sender.canUseCommand(2, "quit_school") ? getPlayer(server, sender, args[0]) : getCommandSenderAsPlayer(sender);

		if (FTBAcademyMod.getTutorialPhase(player) == 1)
		{
			FTBAcademyMod.setTutorialPhase(player, 2);
			World world = server.getWorld(0);

			BlockPos spawnpoint = world.getSpawnPoint();

			while (world.getBlockState(spawnpoint).isFullCube())
			{
				spawnpoint = spawnpoint.up(2);
			}

			player.inventory.clear();
			player.inventory.addItemStackToInventory(new ItemStack(FTBQuestsItems.BOOK));
			ItemStack guideBook = new ItemStack(Items.BOOK);
			guideBook.setTagInfo("guide", new NBTTagString(""));
			guideBook.setTranslatableName("item.ftbguides.book.name");
			player.inventory.addItemStackToInventory(guideBook);
			player.inventoryContainer.detectAndSendChanges();
			TeleporterDimPos.of(spawnpoint, world.provider.getDimension()).teleport(player);
			player.setSpawnPoint(spawnpoint, false);
		}
	}
}