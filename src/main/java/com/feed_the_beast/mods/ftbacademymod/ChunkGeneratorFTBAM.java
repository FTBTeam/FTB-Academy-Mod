package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ChunkGeneratorFTBAM implements IChunkGenerator
{
	private final World world;

	public ChunkGeneratorFTBAM(World w)
	{
		world = w;
	}

	@Override
	public Chunk generateChunk(int x, int z)
	{
		Chunk chunk = new Chunk(world, new ChunkPrimerFTBAM(), x, z);
		Arrays.fill(chunk.getBiomeArray(), (byte) Biome.getIdForBiome(FTBAcademyMod.dimensionBiome));
		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z)
	{
	}

	@Override
	public boolean generateStructures(Chunk chunk, int x, int z)
	{
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		return Collections.emptyList();
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position, boolean findUnexplored)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z)
	{
	}

	@Override
	public boolean isInsideStructure(World world, String structureName, BlockPos pos)
	{
		return false;
	}
}