package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * @author LatvianModder
 */
public class FurnaceAugmentDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation FURNACE_ID = new ResourceLocation("thermalexpansion:machine_furnace");

	@Override
	public boolean test(TileEntity tileEntity)
	{
		if (FURNACE_ID.equals(TileEntity.getKey(tileEntity.getClass())))
		{
			NBTTagCompound nbt = tileEntity.serializeNBT();
			return nbt.getInteger("Level") >= 4 && nbt.getTagList("Augments", Constants.NBT.TAG_COMPOUND).tagCount() >= 4;
		}

		return false;
	}
}