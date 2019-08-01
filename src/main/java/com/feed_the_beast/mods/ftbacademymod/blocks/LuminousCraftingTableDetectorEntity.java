package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class LuminousCraftingTableDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation ALTAR_ID = new ResourceLocation("astralsourcery:tilealtar");

	@Override
	public boolean test(TileEntity tileEntity)
	{
		return ALTAR_ID.equals(TileEntity.getKey(tileEntity.getClass()));
	}
}