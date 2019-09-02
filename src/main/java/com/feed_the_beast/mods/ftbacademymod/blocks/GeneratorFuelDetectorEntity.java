package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.tileentity.TileEntity;

/**
 * @author LatvianModder
 */
public class GeneratorFuelDetectorEntity extends CompletingDetectorEntity
{
	@Override
	public boolean test(TileEntity tileEntity)
	{
		return GeneratorDetectorEntity.GENERATOR_ID.equals(TileEntity.getKey(tileEntity.getClass())) && tileEntity.serializeNBT().getInteger("Energy") > 0;
	}
}