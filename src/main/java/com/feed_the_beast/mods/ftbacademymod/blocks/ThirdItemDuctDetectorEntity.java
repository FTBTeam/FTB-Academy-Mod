package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.init.Blocks;

/**
 * @author LatvianModder
 */
public class ThirdItemDuctDetectorEntity extends ItemDuctDetectorEntity
{
	private static final FilterVariant VARIANT = new FilterVariant("third", 24, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE);

	@Override
	public FilterVariant getVariant()
	{
		return VARIANT;
	}
}