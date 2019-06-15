package com.feed_the_beast.mods.ftbacademymod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * @author LatvianModder
 */
public class ChunkPrimerFTBAM extends ChunkPrimer
{
	private static final IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();

	@Override
	public IBlockState getBlockState(int x, int y, int z)
	{
		return DEFAULT_STATE;
	}

	@Override
	public void setBlockState(int x, int y, int z, IBlockState state)
	{
	}

	@Override
	public int findGroundBlockIdx(int x, int z)
	{
		return 0;
	}
}