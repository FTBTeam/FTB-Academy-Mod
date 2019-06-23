package com.feed_the_beast.mods.ftbacademymod.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.block.FTBQuestsBlocks;
import com.feed_the_beast.ftbquests.tile.TileTaskScreenCore;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageScreenPlaced extends MessageToClient
{
	private BlockPos pos;
	private String team;
	private int task;

	public MessageScreenPlaced()
	{
	}

	public MessageScreenPlaced(BlockPos p, String t, int ta)
	{
		pos = p;
		team = t;
		task = ta;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return NetworkHandlerFTBAM.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writePos(pos);
		data.writeString(team);
		data.writeInt(task);
	}

	@Override
	public void readData(DataIn data)
	{
		pos = data.readPos();
		team = data.readString();
		task = data.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		World w = Minecraft.getMinecraft().world;
		TileEntity tileEntity = w.getTileEntity(pos);

		if (tileEntity instanceof TileTaskScreenCore)
		{
			TileTaskScreenCore screen = (TileTaskScreenCore) tileEntity;
			screen.team = team;
			screen.task = task;
			screen.getTask();
			w.notifyNeighborsOfStateChange(pos, FTBQuestsBlocks.SCREEN, true);
		}
	}
}