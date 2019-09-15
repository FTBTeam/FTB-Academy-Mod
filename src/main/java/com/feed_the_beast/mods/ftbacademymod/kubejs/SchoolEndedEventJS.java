package com.feed_the_beast.mods.ftbacademymod.kubejs;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class SchoolEndedEventJS extends PlayerEventJS
{
	private EntityPlayer player;

	public SchoolEndedEventJS(EntityPlayer p)
	{
		player = p;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}
}