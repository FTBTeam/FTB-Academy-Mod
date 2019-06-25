package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.init.Blocks;

/**
 * @author LatvianModder
 */
public class SecondItemDuctDetectorEntity extends ItemDuctDetectorEntity
{
	private static final FilterVariant VARIANT = new FilterVariant("second", 24, Blocks.DIRT);

	@Override
	public FilterVariant getVariant()
	{
		return VARIANT;
	}
}