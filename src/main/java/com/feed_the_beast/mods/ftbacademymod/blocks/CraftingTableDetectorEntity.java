package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftbquests.quest.ChangeProgress;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public class CraftingTableDetectorEntity extends CompletingDetectorEntity
{
	@Override
	public void update()
	{
		if (!world.isRemote && world.getTotalWorldTime() % getUpdateFrequency() == 0L)
		{
			QuestObject object = ServerQuestFile.INSTANCE.get(id);
			QuestData data = ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player));

			if (object != null && data != null)
			{
				if (world.getBlockState(pos.offset(EnumFacing.UP, distance)).getBlock() == Blocks.CRAFTING_TABLE)
				{
					object.forceProgress(data, ChangeProgress.COMPLETE, true);
					world.setBlockToAir(pos);
				}
			}
		}
	}
}