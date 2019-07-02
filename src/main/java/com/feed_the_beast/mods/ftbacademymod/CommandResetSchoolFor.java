package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CommandResetSchoolFor extends CommandBase
{
	@Override
	public String getName()
	{
		return "reset_school_for";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.reset_school_for.usage";
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 1)
		{
			throw new WrongUsageException("commands.reset_school_for.usage");
		}

		EventHandlerFTBAM.teleportToSchool(getPlayer(server, sender, args[0]));
	}
}