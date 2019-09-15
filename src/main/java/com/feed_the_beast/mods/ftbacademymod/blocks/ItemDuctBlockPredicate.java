package com.feed_the_beast.mods.ftbacademymod.blocks;

import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class ItemDuctBlockPredicate implements BlockPredicate
{
	public static final ResourceLocation DUCT_ID = new ResourceLocation("thermaldynamics:duct_item_transparent");

	private final int flags;
	private final HashSet<String> items;

	public ItemDuctBlockPredicate(int f, String[] i)
	{
		flags = f;
		items = new HashSet<>(i.length);

		for (String s : i)
		{
			items.add(new ResourceLocation(s).toString());
		}
	}

	@Override
	public boolean check(BlockContainerJS b)
	{
		TileEntity tileEntity = b.getEntity();

		if (tileEntity != null && DUCT_ID.equals(TileEntity.getKey(tileEntity.getClass())))
		{
			NBTTagList attachments = tileEntity.writeToNBT(new NBTTagCompound()).getTagList("Attachments", Constants.NBT.TAG_COMPOUND);

			if (attachments.tagCount() == 1)
			{
				NBTTagCompound nbt = attachments.getCompoundTagAt(0);

				if (nbt.getByte("Flags") == flags)
				{
					HashSet<String> inv = new HashSet<>();

					NBTTagList invList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

					for (int i = 0; i < invList.tagCount(); i++)
					{
						NBTTagCompound nbt1 = invList.getCompoundTagAt(i);
						inv.add(nbt1.getString("id"));
					}

					return items.equals(inv);
				}
			}
		}

		return false;
	}
}