package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class CellDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation CELL_ID = new ResourceLocation("thermalexpansion:storage_cell");

	@Override
	public boolean test(TileEntity tileEntity)
	{
		return CELL_ID.equals(TileEntity.getKey(tileEntity.getClass()));
	}
}