package com.feed_the_beast.mods.ftbacademymod.special;

import com.feed_the_beast.mods.ftbacademymod.ItemsFTBAM;
import com.feed_the_beast.mods.ftbacademymod.blocks.BlockDetector;
import com.feed_the_beast.mods.ftbacademymod.blocks.DetectorEntityBase;
import com.feed_the_beast.mods.ftbacademymod.blocks.EnumDetectorType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class SpecialDetector implements SpecialBlockPlacement
{
	public final EnumDetectorType type;
	public final Map<String, String> map;

	public SpecialDetector(EnumDetectorType t, Map<String, String> m)
	{
		type = t;
		map = m;
	}

	@Override
	public void place(World world, BlockPos pos, EntityPlayerMP player)
	{
		world.setBlockState(pos, ItemsFTBAM.DETECTOR.getDefaultState().withProperty(BlockDetector.TYPE, type), 11);
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof DetectorEntityBase)
		{
			((DetectorEntityBase) tileEntity).load(player, map);
			tileEntity.updateContainingBlockInfo();
		}
	}
}