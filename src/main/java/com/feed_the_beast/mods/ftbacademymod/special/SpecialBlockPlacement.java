package com.feed_the_beast.mods.ftbacademymod.special;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface SpecialBlockPlacement
{
	void place(World world, BlockPos pos, EntityPlayerMP player);
}