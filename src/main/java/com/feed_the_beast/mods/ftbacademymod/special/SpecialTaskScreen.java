package com.feed_the_beast.mods.ftbacademymod.special;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftbquests.block.FTBQuestsBlocks;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.ftbquests.tile.TileTaskScreenCore;
import com.feed_the_beast.mods.ftbacademymod.net.MessageScreenPlaced;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class SpecialTaskScreen implements SpecialBlockPlacement
{
	public final int id;
	public final EnumFacing facing;

	public SpecialTaskScreen(int i, EnumFacing f)
	{
		id = i;
		facing = f;
	}

	@Override
	public void place(World world, BlockPos pos, EntityPlayerMP player)
	{
		Task task = ServerQuestFile.INSTANCE.getTask(id);
		String team = FTBLibAPI.getTeam(player.getUniqueID());

		if (task != null && !team.isEmpty())
		{
			world.setBlockState(pos, FTBQuestsBlocks.SCREEN.getDefaultState().withProperty(BlockHorizontal.FACING, facing), 3);
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileTaskScreenCore)
			{
				TileTaskScreenCore screen = (TileTaskScreenCore) tileEntity;
				screen.team = team;
				screen.task = task.id;
				screen.updateContainingBlockInfo();
				screen.getTask();
				world.notifyNeighborsOfStateChange(pos, FTBQuestsBlocks.SCREEN, true);
				new MessageScreenPlaced(pos, team, task.id).sendTo(player);
			}
		}
	}
}