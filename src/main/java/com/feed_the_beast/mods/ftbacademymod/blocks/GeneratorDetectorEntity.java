package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class GeneratorDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation DUCT_ID = new ResourceLocation("actuallyadditions:coalgenerator");

	@Override
	public boolean test(TileEntity tileEntity)
	{
		return DUCT_ID.equals(TileEntity.getKey(tileEntity.getClass())) && tileEntity.serializeNBT().getInteger("Energy") > 0;
	}
}