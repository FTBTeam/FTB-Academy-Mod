package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.util.BlockUtils;
import com.feed_the_beast.ftbquests.quest.ChangeProgress;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.mods.ftbacademymod.util.DetectorEntry;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class BlockDetectorEntity extends TileEntity implements ITickable
{
	public UUID player = new UUID(0L, 0L);
	public String id = "";
	public int distance = 2;
	public String type = "";

	public void load(EntityPlayerMP ep, String t, Map<String, String> map)
	{
		player = ep.getUniqueID();
		type = t;
		id = map.getOrDefault("id", "");
		distance = Integer.parseInt(map.getOrDefault("dist", "2"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setUniqueId("player", player);
		nbt.setString("type", type);
		nbt.setString("object", id);
		nbt.setInteger("dist", distance);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		player = nbt.getUniqueId("player");
		type = nbt.getString("type");
		id = nbt.getString("object");
		distance = nbt.getInteger("dist");
		super.readFromNBT(nbt);
	}

	@Override
	public void update()
	{
		if (world.isRemote)
		{
			return;
		}

		DetectorEntry detector = DetectorEntry.MAP.get(type);

		if (detector != null && world.getTotalWorldTime() % 20L == 0L)
		{
			QuestObject object = ServerQuestFile.INSTANCE.get(ServerQuestFile.INSTANCE.getID(id));
			QuestData data = ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player));

			if (object != null && data != null)
			{
				if (object instanceof Quest && !((Quest) object).canStartTasks(data) || object instanceof Task && !((Task) object).quest.canStartTasks(data))
				{
					return;
				}

				BlockContainerJS block = new BlockContainerJS(world, pos.offset(EnumFacing.UP, distance));

				if (detector.predicate.check(block))
				{
					object.forceProgress(data, ChangeProgress.COMPLETE, true);

					if (detector.after != null)
					{
						NBTCompoundJS d = block.getEntityData();
						detector.after.accept(d);
						block.setEntityData(d);
						BlockUtils.notifyBlockUpdate(block.getWorld().minecraftWorld, block.getPos(), null);
					}

					world.setBlockToAir(pos);
				}
			}
		}
	}
}