package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftbquests.quest.ITeamData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class BlockManaDetector extends Block
{
	public static final ResourceLocation POOL_ID = new ResourceLocation("botania:pool");

	public BlockManaDetector()
	{
		super(Material.BARRIER);
		setBlockUnbreakable();
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new Entity();
	}

	@Override
	@Deprecated
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.INVISIBLE;
	}

	public static class Entity extends TileEntity implements ITickable
	{
		public int distance = 0;
		public int id = 0;
		public UUID player = new UUID(0, 0);

		@Override
		public void update()
		{
			if (!world.isRemote && world.getTotalWorldTime() % 20L == 0L)
			{
				TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP, distance));

				if (tileEntity != null && POOL_ID.equals(TileEntity.getKey(tileEntity.getClass())) && tileEntity.writeToNBT(new NBTTagCompound()).getLong("mana") > 0L)
				{
					QuestObject object = ServerQuestFile.INSTANCE.get(id);

					if (object != null)
					{
						ITeamData data = ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player));

						if (data != null)
						{
							object.onCompleted(data, Collections.emptyList());
							world.setBlockToAir(pos);
						}
					}
				}
			}
		}
	}
}