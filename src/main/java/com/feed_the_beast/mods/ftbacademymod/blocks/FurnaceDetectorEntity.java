package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class FurnaceDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation DUCT_ID = new ResourceLocation("thermalexpansion:machine_furnace");

	@Override
	public boolean test(TileEntity tileEntity)
	{
		if (DUCT_ID.equals(TileEntity.getKey(tileEntity.getClass())))
		{
			return tileEntity.serializeNBT().getInteger("Energy") > 0;
		}

		return false;
	}
}