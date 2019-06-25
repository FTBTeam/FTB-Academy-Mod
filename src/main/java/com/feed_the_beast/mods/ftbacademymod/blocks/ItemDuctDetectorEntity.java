package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;

/**
 * @author LatvianModder
 */
public abstract class ItemDuctDetectorEntity extends CompletingDetectorEntity
{
	public static final ResourceLocation DUCT_ID = new ResourceLocation("thermaldynamics:duct_item_transparent");

	public abstract FilterVariant getVariant();

	@Override
	public boolean test(TileEntity tileEntity)
	{
		if (DUCT_ID.equals(TileEntity.getKey(tileEntity.getClass())))
		{
			NBTTagList attachments = tileEntity.writeToNBT(new NBTTagCompound()).getTagList("Attachments", Constants.NBT.TAG_COMPOUND);

			if (attachments.tagCount() == 1)
			{
				NBTTagCompound nbt = attachments.getCompoundTagAt(0);
				FilterVariant variant = getVariant();

				if (nbt.getByte("Flags") == variant.flags)
				{
					HashSet<FilterItem> inv = new HashSet<>();

					NBTTagList invList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

					for (int i = 0; i < invList.tagCount(); i++)
					{
						NBTTagCompound nbt1 = invList.getCompoundTagAt(i);
						inv.add(new FilterItem(nbt1.getString("id"), nbt1.getShort("Damage")));
					}

					return variant.items.equals(inv);
				}
			}
		}

		return false;
	}
}