package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.init.Blocks;

/**
 * @author LatvianModder
 */
public class FirstItemDuctDetectorEntity extends ItemDuctDetectorEntity
{
	private static final FilterVariant VARIANT = new FilterVariant("first", 25, Blocks.DIRT, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE);

	@Override
	public FilterVariant getVariant()
	{
		return VARIANT;
	}
}