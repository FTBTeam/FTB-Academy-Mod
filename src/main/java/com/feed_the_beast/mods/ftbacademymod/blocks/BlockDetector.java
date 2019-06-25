package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BlockDetector extends Block
{
	public static final PropertyEnum<EnumDetectorType> TYPE = PropertyEnum.create("type", EnumDetectorType.class);

	public BlockDetector()
	{
		super(Material.BARRIER);
		setBlockUnbreakable();
		setTranslationKey(FTBAcademyMod.MOD_ID + ".detector");
		setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumDetectorType.MANA));
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(TYPE).metadata;
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta)
	{
		EnumDetectorType type = EnumDetectorType.META_MAP.get(meta);
		return type == null ? getDefaultState() : getDefaultState().withProperty(TYPE, type);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return state.getValue(TYPE).supplier.get();
	}
}