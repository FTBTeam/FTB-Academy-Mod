package com.feed_the_beast.mods.ftbacademymod.kubejs;

import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class DetectorEntry
{
	public final String id;
	public final BlockPredicate predicate;
	private Consumer<NBTCompoundJS> after;

	public DetectorEntry(String i, BlockPredicate p)
	{
		id = i;
		predicate = p;
	}

	public DetectorEntry after(Consumer<NBTCompoundJS> a)
	{
		after = a;
		return this;
	}

	@Nullable
	public Consumer<NBTCompoundJS> getAfter()
	{
		return after;
	}
}