package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.util.BlockUtils;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class EntityDetectorPredicate implements DetectorPredicate
{
	@FunctionalInterface
	public interface WithData
	{
		boolean check(NBTCompoundJS data);
	}

	@FunctionalInterface
	public interface After
	{
		void after(NBTCompoundJS data);
	}

	private final ResourceLocation id;
	private WithData withData;
	private After after;

	public EntityDetectorPredicate(ResourceLocation _id)
	{
		id = _id;
	}

	@Override
	public boolean check(BlockContainerJS b)
	{
		TileEntity tileEntity = b.getEntity();

		if (tileEntity != null && id.equals(TileEntity.getKey(tileEntity.getClass())))
		{
			return withData == null || withData.check(NBTBaseJS.of(tileEntity.serializeNBT()).asCompound());
		}

		return false;
	}

	@Override
	public void after(BlockContainerJS block)
	{
		if (after != null)
		{
			NBTCompoundJS data = block.getEntityData();
			after.after(data);
			block.setEntityData(data);
			BlockUtils.notifyBlockUpdate(block.getWorld().world, block.getPos(), null);
		}
	}

	public EntityDetectorPredicate data(WithData wd)
	{
		withData = wd;
		return this;
	}

	public EntityDetectorPredicate after(After a)
	{
		after = a;
		return this;
	}
}