package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
		EntityPlayerMP playerMP = getCommandSenderAsPlayer(sender);

		if (FTBAcademyMod.isInSchool(playerMP))
		{
			EventHandlerFTBAM.finishSchool(playerMP);
		}
	}
}