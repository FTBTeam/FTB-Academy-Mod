package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class ManaDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation POOL_ID = new ResourceLocation("botania:pool");

	@Override
	public boolean test(TileEntity tileEntity)
	{
		return POOL_ID.equals(TileEntity.getKey(tileEntity.getClass())) && tileEntity.writeToNBT(new NBTTagCompound()).getLong("mana") > 0L;
	}
}