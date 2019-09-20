package com.feed_the_beast.mods.ftbacademymod.kubejs;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import com.feed_the_beast.mods.ftbacademymod.blocks.ItemDuctBlockPredicate;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicateDataCheck;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.player.PlayerJS;

/**
 * @author LatvianModder
 */
public class FTBAcademyModWrapper
{
	public boolean isInSchool(PlayerJS player)
	{
		return FTBAcademyMod.isInSchool(player.getPlayerEntity());
	}

	public DetectorEntry addDetector(String id, BlockPredicate predicate)
	{
		DetectorEntry entry = new DetectorEntry(id, predicate);
		KubeJSIntegration.DETECTORS.put(id, entry);
		return entry;
	}

	public BlockEntityPredicateDataCheck intMatchOrMore(String name, int value)
	{
		return data -> data.get(name).asInt() >= value;
	}

	public ItemDuctBlockPredicate duct(int flags, String[] items)
	{
		return new ItemDuctBlockPredicate(flags, items);
	}
}