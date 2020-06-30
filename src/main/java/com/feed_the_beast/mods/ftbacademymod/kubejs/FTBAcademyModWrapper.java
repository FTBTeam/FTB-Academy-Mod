package com.feed_the_beast.mods.ftbacademymod.kubejs;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import dev.latvian.kubejs.player.PlayerJS;

/**
 * @author LatvianModder
 */
public class FTBAcademyModWrapper
{
	public boolean isInSchool(PlayerJS player)
	{
		return FTBAcademyMod.isInSchool(player.minecraftPlayer);
	}
}