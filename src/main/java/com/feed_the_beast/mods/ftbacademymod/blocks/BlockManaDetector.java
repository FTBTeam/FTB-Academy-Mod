package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftbquests.net.edit.MessageChangeProgressResponse;
import com.feed_the_beast.ftbquests.quest.EnumChangeProgress;
import com.feed_the_beast.ftbquests.quest.ITeamData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class BlockManaDetector extends BlockDetectorBase
{
	public static final ResourceLocation POOL_ID = new ResourceLocation("botania:pool");

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new Entity();
	}

	public static class Entity extends TileEntity implements ITickable
	{
		public int distance = 0;
		public int id = 0;
		public UUID player = new UUID(0, 0);

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
			distance = compound.getInteger("dist");
			id = compound.getInteger("object");
			player = compound.getUniqueId("player");
			return super.writeToNBT(compound);
		}

		@Override
		public void readFromNBT(NBTTagCompound compound)
		{
			compound.setInteger("dist", distance);
			compound.setInteger("object", id);
			compound.setUniqueId("player", player);
			super.readFromNBT(compound);
		}

		@Override
		public void update()
		{
			if (!world.isRemote && world.getTotalWorldTime() % 20L == 0L)
			{
				QuestObject object = ServerQuestFile.INSTANCE.get(id);
				ITeamData data = ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player));

				if (object != null && data != null)
				{
					TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP, distance));

					if (tileEntity != null && POOL_ID.equals(TileEntity.getKey(tileEntity.getClass())) && tileEntity.writeToNBT(new NBTTagCompound()).getLong("mana") > 0L)
					{
						EnumChangeProgress.sendUpdates = false;
						object.changeProgress(data, EnumChangeProgress.COMPLETE);
						EnumChangeProgress.sendUpdates = true;
						new MessageChangeProgressResponse(data.getTeamUID(), object.id, EnumChangeProgress.COMPLETE).sendToAll();
						world.setBlockToAir(pos);
					}
				}
			}
		}
	}
}