package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author LatvianModder
 */
public class FilterItem
{
	public final String id;
	public final int damage;

	public FilterItem(String i, int d)
	{
		id = i;
		damage = d;
	}

	public FilterItem(String i)
	{
		this(i, 0);
	}

	public FilterItem(IForgeRegistryEntry entry)
	{
		this(entry.getRegistryName().toString());
	}

	@Override
	public int hashCode()
	{
		return id.hashCode() + damage;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof FilterItem)
		{
			FilterItem i = (FilterItem) obj;
			return damage == i.damage && id.equals(i.id);
		}

		return false;
	}
}