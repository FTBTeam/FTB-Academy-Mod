package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftbquests.quest.ChangeProgress;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestFile;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.Task;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class CompletingDetectorEntity extends DetectorEntityBase
{
	public UUID player = new UUID(0L, 0L);
	public int id = 0;
	public int distance = 0;

	@Override
	public void write(NBTTagCompound nbt)
	{
		nbt.setUniqueId("player", player);
		nbt.setInteger("object", id);
		nbt.setInteger("dist", distance);
	}

	@Override
	public void read(NBTTagCompound nbt)
	{
		player = nbt.getUniqueId("player");
		id = nbt.getInteger("object");
		distance = nbt.getInteger("dist");
	}

	@Override
	public void load(EntityPlayerMP ep, Map<String, String> map)
	{
		player = ep.getUniqueID();
		id = QuestFile.getID(map.getOrDefault("id", ""));
		distance = Integer.parseInt(map.getOrDefault("dist", "2"));
	}

	public long getUpdateFrequency()
	{
		return 20L;
	}

	public boolean test(TileEntity tileEntity)
	{
		return false;
	}

	@Override
	public void update()
	{
		if (!world.isRemote && world.getTotalWorldTime() % getUpdateFrequency() == 0L)
		{
			QuestObject object = ServerQuestFile.INSTANCE.get(id);
			QuestData data = ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player));

			if (object != null && data != null)
			{
				if (object instanceof Quest && !((Quest) object).canStartTasks(data) || object instanceof Task && !((Task) object).quest.canStartTasks(data))
				{
					return;
				}

				TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP, distance));

				if (tileEntity != null && test(tileEntity))
				{
					object.forceProgress(data, ChangeProgress.COMPLETE, true);
					world.setBlockToAir(pos);
				}
			}
		}
	}
}