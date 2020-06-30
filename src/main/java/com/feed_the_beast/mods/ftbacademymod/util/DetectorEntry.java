package com.feed_the_beast.mods.ftbacademymod.util;

import dev.latvian.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicateDataCheck;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class DetectorEntry
{
	public static final Map<String, DetectorEntry> MAP = new HashMap<>();

	public static void init()
	{
		addDetector("crafting_table", new BlockIDPredicate("minecraft:crafting_table"));
		addDetector("first_item_duct", duct(25, "minecraft:dirt", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:diamond_ore"));
		addDetector("second_item_duct", duct(24, "minecraft:dirt"));
		addDetector("third_item_duct", duct(24, "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:diamond_ore"));
		addDetector("flux_duct", new BlockEntityPredicate("thermaldynamics:duct_energy_basic"));
		addDetector("generator", new BlockEntityPredicate("actuallyadditions:coalgenerator"));
		addDetector("generator_fuel", new BlockEntityPredicate("actuallyadditions:coalgenerator").data(intMatchOrMore("Energy", 1)));
		addDetector("furnace", new BlockEntityPredicate("thermalexpansion:machine_furnace").data(intMatchOrMore("Energy", 1)));
		addDetector("tank", new BlockEntityPredicate("thermalexpansion:storage_tank").data(intMatchOrMore("Amount", 1)));
		addDetector("luminous_crafting_table", new BlockEntityPredicate("astralsorcery:tilealtar"));
		addDetector("endo", new BlockEntityPredicate("botania:specialflower"));
		addDetector("endo_fuel", new BlockEntityPredicate("botania:specialflower").data(data -> data.get("subTileCmp").asCompound().get("burnTime").asInt() >= 1));
		addDetector("mana", new BlockEntityPredicate("botania:pool").data(intMatchOrMore("mana", 1))).after(data -> data.set("mana", 20000));
	}

	public static DetectorEntry addDetector(String id, BlockPredicate predicate)
	{
		DetectorEntry entry = new DetectorEntry(id, predicate);
		MAP.put(id, entry);
		return entry;
	}

	public static BlockEntityPredicateDataCheck intMatchOrMore(String name, int value)
	{
		return data -> data.get(name).asInt() >= value;
	}

	public static ItemDuctBlockPredicate duct(int flags, String... items)
	{
		return new ItemDuctBlockPredicate(flags, items);
	}

	public final String id;
	public final BlockPredicate predicate;
	public Consumer<NBTCompoundJS> after;

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
}