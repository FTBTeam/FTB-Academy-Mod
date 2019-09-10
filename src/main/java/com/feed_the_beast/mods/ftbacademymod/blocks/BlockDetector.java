package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BlockDetector extends Block
{
	public BlockDetector()
	{
		super(Material.BARRIER);
		setBlockUnbreakable();
		setTranslationKey(FTBAcademyMod.MOD_ID + ".detector");
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new BlockDetectorEntity();
	}
}