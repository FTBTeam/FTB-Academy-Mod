package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class TankDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation TANK_ID = new ResourceLocation("thermalexpansion:storage_tank");

	@Override
	public boolean test(TileEntity tileEntity)
	{
		if (TANK_ID.equals(TileEntity.getKey(tileEntity.getClass())))
		{
			return tileEntity.serializeNBT().getInteger("Amount") > 0;
		}

		return false;
	}
}