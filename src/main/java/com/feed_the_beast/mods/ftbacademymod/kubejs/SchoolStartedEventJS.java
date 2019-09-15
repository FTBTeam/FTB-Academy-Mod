package com.feed_the_beast.mods.ftbacademymod.kubejs;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class SchoolStartedEventJS extends PlayerEventJS
{
	private EntityPlayer player;
	private boolean restart;

	public SchoolStartedEventJS(EntityPlayer p, boolean r)
	{
		player = p;
		restart = r;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}

	public boolean isRestart()
	{
		return restart;
	}
}