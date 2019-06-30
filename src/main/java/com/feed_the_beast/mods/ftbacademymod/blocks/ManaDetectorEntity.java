package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.util.BlockUtils;
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
		if (POOL_ID.equals(TileEntity.getKey(tileEntity.getClass())))
		{
			NBTTagCompound nbt = tileEntity.serializeNBT();

			if (nbt.getLong("mana") > 0L)
			{
				nbt.setLong("mana", 50000L);
				tileEntity.deserializeNBT(nbt);
				BlockUtils.notifyBlockUpdate(world, tileEntity.getPos(), null);
				return true;
			}
		}

		return false;
	}
}