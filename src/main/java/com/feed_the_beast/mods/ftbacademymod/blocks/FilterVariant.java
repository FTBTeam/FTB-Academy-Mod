package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class FilterVariant
{
	public final int flags;
	public final HashSet<FilterItem> items;

	public FilterVariant(int f, FilterItem... i)
	{
		flags = f;
		items = new HashSet<>(Arrays.asList(i));
	}

	public FilterVariant(String n, int f, IForgeRegistryEntry... i)
	{
		flags = f;
		FilterItem[] items0 = new FilterItem[i.length];

		for (int j = 0; j < i.length; j++)
		{
			items0[j] = new FilterItem(i[j]);
		}

		items = new HashSet<>(Arrays.asList(items0));
	}
}