package com.feed_the_beast.mods.ftbacademymod.blocks;

import dev.latvian.kubejs.world.BlockContainerJS;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface DetectorPredicate
{
	boolean check(BlockContainerJS block);

	default void after(BlockContainerJS block)
	{
	}

	default long checkTimer()
	{
		return 20L;
	}
}