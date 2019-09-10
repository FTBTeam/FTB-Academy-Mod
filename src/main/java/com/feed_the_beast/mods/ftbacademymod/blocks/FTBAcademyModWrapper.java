package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import com.feed_the_beast.mods.ftbacademymod.KubeJSIntegration;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.util.ID;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FTBAcademyModWrapper
{
	public boolean isInSchool(PlayerJS player)
	{
		return FTBAcademyMod.isInSchool(player.playerEntity);
	}

	public void addDetector(String id, DetectorPredicate predicate)
	{
		KubeJSIntegration.DETECTORS.put(id, predicate);
	}

	public void addDetectorForBlock(String id, @Nullable Object blockId)
	{
		addDetector(id, new BlockDetectorPredicate(ID.of(blockId).mc()));
	}

	public EntityDetectorPredicate addDetectorForEntity(String id, @Nullable Object entityId)
	{
		EntityDetectorPredicate entity = new EntityDetectorPredicate(ID.of(entityId).mc());
		addDetector(id, entity);
		return entity;
	}

	public EntityDetectorPredicate.WithData intMatchOrMore(String name, int value)
	{
		return data -> data.get(name).asInt() >= value;
	}

	public void addDetectorForItemDuct(String id, int flags, String[] items)
	{
		addDetector(id, new ItemDuctDetectorPredicate(flags, items));
	}

	public void canPlaceOn(String item, String block)
	{
	}
}