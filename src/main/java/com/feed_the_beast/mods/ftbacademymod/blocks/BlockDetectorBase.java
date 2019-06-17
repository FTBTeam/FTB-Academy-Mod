package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;

/**
 * @author LatvianModder
 */
public class BlockDetectorBase extends Block
{
	public BlockDetectorBase()
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

	@Override
	@Deprecated
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.INVISIBLE;
	}
}