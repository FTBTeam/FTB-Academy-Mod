package com.feed_the_beast.mods.ftbacademymod.special;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDuctDetector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class SpecialDuctDetector implements SpecialBlockPlacement
{
	public final int id;
	public final int dist;
	public final String variant;

	public SpecialDuctDetector(int i, int d, String v)
	{
		id = i;
		dist = d;
		variant = v;
	}

	@Override
	public void place(World world, BlockPos pos, EntityPlayerMP player)
	{
		world.setBlockState(pos, FTBAcademyMod.DUCT_DETECTOR.getDefaultState(), 3);
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof BlockDuctDetector.Entity)
		{
			BlockDuctDetector.Entity ductDetector = (BlockDuctDetector.Entity) tileEntity;
			ductDetector.id = id;
			ductDetector.player = player.getUniqueID();
			ductDetector.distance = dist;
			ductDetector.variant = BlockDuctDetector.Entity.FilterVariant.VARIANT_MAP.get(variant);
		}
	}
}