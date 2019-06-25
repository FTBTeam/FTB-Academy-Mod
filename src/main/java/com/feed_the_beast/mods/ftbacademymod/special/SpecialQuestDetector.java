package com.feed_the_beast.mods.ftbacademymod.special;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftbquests.block.FTBQuestsBlocks;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.tile.TileProgressDetector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class SpecialQuestDetector implements SpecialBlockPlacement
{
	public final int id;

	public SpecialQuestDetector(int i)
	{
		id = i;
	}

	@Override
	public void place(World world, BlockPos pos, EntityPlayerMP player)
	{
		QuestObject object = ServerQuestFile.INSTANCE.get(id);
		String team = FTBLibAPI.getTeam(player.getUniqueID());

		if (object != null && !team.isEmpty())
		{
			world.setBlockState(pos, FTBQuestsBlocks.PROGRESS_DETECTOR.getDefaultState(), 3);
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileProgressDetector)
			{
				((TileProgressDetector) tileEntity).team = team;
				((TileProgressDetector) tileEntity).object = object.id;
			}
		}
	}
}