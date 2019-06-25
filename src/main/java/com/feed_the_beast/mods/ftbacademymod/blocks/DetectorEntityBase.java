package com.feed_the_beast.mods.ftbacademymod.blocks;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class DetectorEntityBase extends TileEntity implements ITickable
{
	public void write(NBTTagCompound nbt)
	{
	}

	public void read(NBTTagCompound nbt)
	{
	}

	public void load(EntityPlayerMP player, Map<String, String> map)
	{
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		write(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		read(nbt);
		super.readFromNBT(nbt);
	}

	@Override
	public void update()
	{
	}
}