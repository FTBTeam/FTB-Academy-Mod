package com.feed_the_beast.mods.ftbacademymod.special;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockManaDetector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class SpecialManaDetector implements SpecialBlockPlacement
{
	public final int id;
	public final int dist;

	public SpecialManaDetector(int i, int d)
	{
		id = i;
		dist = d;
	}

	@Override
	public void place(World world, BlockPos pos, EntityPlayerMP player)
	{
		world.setBlockState(pos, FTBAcademyMod.MANA_DETECTOR.getDefaultState(), 3);
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof BlockManaDetector.Entity)
		{
			BlockManaDetector.Entity manaDetector = (BlockManaDetector.Entity) tileEntity;
			manaDetector.id = id;
			manaDetector.player = player.getUniqueID();
			manaDetector.distance = dist;
		}
	}
}