package com.feed_the_beast.mods.ftbacademymod.util;

import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.world.BlockContainerJS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class LeftClick
{
	private static final List<LeftClick> LIST = new ArrayList<>();

	public static boolean canLeftClick(BlockContainerJS b)
	{
		for (LeftClick i : LIST)
		{
			if (i.block.check(b))
			{
				return true;
			}
		}

		return false;
	}

	public static void init()
	{
		add().block("storagedrawers:compdrawers");
		add().block("storagedrawers:basicdrawers");
	}

	public static LeftClick add()
	{
		LeftClick i = new LeftClick();
		LIST.add(i);
		return i;
	}

	public BlockPredicate block;

	public LeftClick block(BlockPredicate p)
	{
		block = p;
		return this;
	}

	public LeftClick block(String id)
	{
		return block(new BlockIDPredicate(id));
	}
}
