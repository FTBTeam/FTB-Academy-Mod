package com.feed_the_beast.mods.ftbacademymod;

import net.minecraftforge.common.config.Config;

/**
 * @author LatvianModder
 */
@Config(modid = FTBAcademyMod.MOD_ID)
public class ConfigFTBAM
{
	public static final General general = new General();

	public static class General
	{
		public int dimension_id = -3;
		public String dimension_biome = "minecraft:plains";
	}
}