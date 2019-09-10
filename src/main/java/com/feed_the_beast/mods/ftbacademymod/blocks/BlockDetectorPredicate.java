package com.feed_the_beast.mods.ftbacademymod.blocks;

import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class BlockDetectorPredicate implements DetectorPredicate
{
	private final ResourceLocation id;
	private Block block = null;

	public BlockDetectorPredicate(ResourceLocation _id)
	{
		id = _id;
	}

	@Override
	public boolean check(BlockContainerJS b)
	{
		if (block == null)
		{
			block = Block.REGISTRY.getObject(id);

			if (block == null)
			{
				block = Blocks.AIR;
			}
		}

		return block != Blocks.AIR && b.getBlockState().getBlock() == block;
	}
}