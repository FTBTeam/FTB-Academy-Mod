package com.feed_the_beast.mods.ftbacademymod.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.IStringSerializable;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum EnumDetectorType implements IStringSerializable
{
	MANA("mana", 0, ManaDetectorEntity.class, ManaDetectorEntity::new),
	FIRST_ITEM_DUCT("first_item_duct", 1, FirstItemDuctDetectorEntity.class, FirstItemDuctDetectorEntity::new),
	SECOND_ITEM_DUCT("second_item_duct", 2, SecondItemDuctDetectorEntity.class, SecondItemDuctDetectorEntity::new),
	THIRD_ITEM_DUCT("third_item_duct", 3, ThirdItemDuctDetectorEntity.class, ThirdItemDuctDetectorEntity::new),
	FLUX_DUCT("flux_duct", 4, FluxDuctDetectorEntity.class, FluxDuctDetectorEntity::new),
	GENERATOR("generator", 5, GeneratorDetectorEntity.class, GeneratorDetectorEntity::new),
	FURNACE("furnace", 6, FurnaceDetectorEntity.class, FurnaceDetectorEntity::new),
	FURNACE_AUG("furnace_aug", 7, FurnaceAugmentDetectorEntity.class, FurnaceAugmentDetectorEntity::new),
	CELL("cell", 8, CellDetectorEntity.class, CellDetectorEntity::new),
	TANK("tank", 9, TankDetectorEntity.class, TankDetectorEntity::new);

	public static final EnumDetectorType[] VALUES = values();
	public static final Int2ObjectOpenHashMap<EnumDetectorType> META_MAP = new Int2ObjectOpenHashMap<>();
	public static final HashMap<String, EnumDetectorType> NAME_MAP = new HashMap<>();

	static
	{
		for (EnumDetectorType type : VALUES)
		{
			META_MAP.put(type.metadata, type);
			NAME_MAP.put(type.name, type);
		}
	}

	public final String name;
	public final int metadata;
	public final Class<? extends DetectorEntityBase> clazz;
	public final Supplier<DetectorEntityBase> supplier;

	EnumDetectorType(String n, int m, Class<? extends DetectorEntityBase> c, Supplier<DetectorEntityBase> s)
	{
		name = n;
		metadata = m;
		clazz = c;
		supplier = s;
	}

	@Override
	public String getName()
	{
		return name;
	}
}