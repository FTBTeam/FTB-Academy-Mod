package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftbquests.FTBQuests;
import dev.latvian.kubejs.KubeJS;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(
		modid = FTBAcademyMod.MOD_ID,
		name = FTBAcademyMod.MOD_NAME,
		version = FTBAcademyMod.VERSION,
		dependencies = FTBLib.THIS_DEP + ";required-after:" + FTBQuests.MOD_ID + ";required-before:" + KubeJS.MOD_ID,
		acceptableRemoteVersions = "*"
)
public class FTBAcademyMod
{
	public static final String MOD_ID = "ftbacademymod";
	public static final String MOD_NAME = "FTB Academy Mod";
	public static final String VERSION = "0.0.0.ftbacademymod";

	public static DimensionType dimensionType;
	public static Biome dimensionBiome;

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
		event.registerServerCommand(new CommandResetSchoolFor());
	}

	public static int getSchoolPhase(EntityPlayer player)
	{
		if (GameStageHelper.hasStage(player, "ftba_in_school"))
		{
			return 1;
		}
		else if (GameStageHelper.hasStage(player, "ftba_finished_school"))
		{
			return 2;
		}

		int oldp = NBTUtils.getPersistedData(player, false).getByte("ftbacademy_tutorial_phase");

		if (oldp > 0)
		{
			setSchoolPhase(player, oldp == 2 ? 2 : 0);
			NBTUtils.getPersistedData(player, false).removeTag("ftbacademy_tutorial_phase");
			player.setGameType(GameType.SURVIVAL);
			System.out.println("Found old FTB Academy tutorial phase, moving to gamestages");
		}

		return oldp;
	}

	public static void setSchoolPhase(EntityPlayer player, int tutorialPhase)
	{
		if (tutorialPhase == 0)
		{
			GameStageHelper.removeStage(player, "ftba_in_school");
			GameStageHelper.removeStage(player, "ftba_finished_school");
		}
		else if (tutorialPhase == 1)
		{
			GameStageHelper.removeStage(player, "ftba_finished_school");
			GameStageHelper.addStage(player, "ftba_in_school");
		}
		else if (tutorialPhase == 2)
		{
			GameStageHelper.removeStage(player, "ftba_in_school");
			GameStageHelper.addStage(player, "ftba_finished_school");
		}

		GameStageHelper.syncPlayer(player);
	}

	public static boolean isInSchool(EntityPlayer player)
	{
		return getSchoolPhase(player) == 1;
	}
}