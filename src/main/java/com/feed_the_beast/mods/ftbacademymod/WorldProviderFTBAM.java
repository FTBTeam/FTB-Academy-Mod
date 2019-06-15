package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

/**
 * @author LatvianModder
 */
public class WorldProviderFTBAM extends WorldProvider
{
	private static final BlockPos SPAWN_POINT = new BlockPos(8, 64, 8);
	public int schoolsSpawned = 0;

	@Override
	public DimensionType getDimensionType()
	{
		return FTBAcademyMod.dimensionType;
	}

	@Override
	public void init()
	{
		hasSkyLight = true;
		biomeProvider = new BiomeProviderSingle(FTBAcademyMod.dimensionBiome);
		schoolsSpawned = world.getWorldInfo().getDimensionData(world.provider.getDimension()).getInteger("islands_spawned");
		setAllowedSpawnTypes(false, false);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorFTBAM(world);
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return true;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		return true;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks)
	{
		return 0.5F;
	}

	@Override
	public int getMoonPhase(long worldTime)
	{
		return 0;
	}

	@Override
	public boolean canRespawnHere()
	{
		return true;
	}

	@Override
	public boolean doesXZShowFog(int x, int z)
	{
		return false;
	}

	@Override
	public BlockPos getRandomizedSpawnPoint()
	{
		return SPAWN_POINT;
	}

	@Override
	public BlockPos getSpawnPoint()
	{
		return SPAWN_POINT;
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double z, double rotation)
	{
		return true;
	}

	@Override
	public Biome getBiomeForCoords(BlockPos pos)
	{
		return FTBAcademyMod.dimensionBiome;
	}

	@Override
	public void onWorldSave()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("islands_spawned", schoolsSpawned);
		world.getWorldInfo().setDimensionData(world.provider.getDimension(), nbt);
	}
}