package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftbquests.FTBQuests;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
		modid = FTBAcademyMod.MOD_ID,
		name = FTBAcademyMod.MOD_NAME,
		version = FTBAcademyMod.VERSION,
		dependencies = FTBLib.THIS_DEP + ";after:" + FTBQuests.MOD_ID
)
public class FTBAcademyMod
{
	public static final String MOD_ID = "ftbacademymod";
	public static final String MOD_NAME = "FTB Academy Mod";
	public static final String VERSION = "0.0.0.ftbacademymod";

	public static DimensionType dimensionType;
	public static Biome dimensionBiome;

	@GameRegistry.ObjectHolder(MOD_ID + ":mana_detector")
	public static Block MANA_DETECTOR;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		dimensionType = DimensionType.register("ftbacademy", "_ftbacademy", ConfigFTBAM.general.dimension_id, WorldProviderFTBAM.class, false);
		dimensionBiome = Biomes.PLAINS;
		DimensionManager.registerDimension(ConfigFTBAM.general.dimension_id, dimensionType);
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event)
	{
		Biome b = ForgeRegistries.BIOMES.getValue(new ResourceLocation(ConfigFTBAM.general.dimension_biome));

		if (b != null)
		{
			dimensionBiome = b;
		}
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandQuitSchool());
		event.registerServerCommand(new CommandResetSchool());
	}

	public static int getTutorialPhase(EntityPlayer player)
	{
		return NBTUtils.getPersistedData(player, false).getByte("ftbacademy_tutorial_phase");
	}

	public static void setTutorialPhase(EntityPlayer player, long tutorialPhase)
	{
		if (tutorialPhase <= 0L)
		{
			NBTUtils.getPersistedData(player, false).removeTag("ftbacademy_tutorial_phase");
		}
		else
		{
			NBTUtils.getPersistedData(player, true).setByte("ftbacademy_tutorial_phase", (byte) tutorialPhase);
		}
	}
}