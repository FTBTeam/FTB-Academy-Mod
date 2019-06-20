package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
public class ItemsFTBAM
{
	@GameRegistry.ObjectHolder(FTBAcademyMod.MOD_ID + ":mana_detector")
	public static Block MANA_DETECTOR;

	@GameRegistry.ObjectHolder(FTBAcademyMod.MOD_ID + ":duct_detector")
	public static Block DUCT_DETECTOR;

	@GameRegistry.ObjectHolder("astralsorcery:blockaltar")
	public static Item ALTAR;

	@GameRegistry.ObjectHolder("botania:specialflower")
	public static Item FLOWER;
}