package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
public class ItemsFTBAM
{
	@GameRegistry.ObjectHolder(FTBAcademyMod.MOD_ID + ":detector")
	public static Block DETECTOR;

	@GameRegistry.ObjectHolder("tconstruct:tooltables")
	public static Item TOOLTABLE;
}