package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CommandResetSchool extends CommandBase
{
	@Override
	public String getName()
	{
		return "reset_school";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.reset_school.usage";
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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (FTBAcademyMod.isInTutorial(player))
		{
			EventHandlerFTBAM.teleportToSchool(player, true);
		}
	}
}