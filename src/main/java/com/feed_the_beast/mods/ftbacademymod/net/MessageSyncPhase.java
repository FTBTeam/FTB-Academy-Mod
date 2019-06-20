package com.feed_the_beast.mods.ftbacademymod.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageSyncPhase extends MessageToClient
{
	private int phase;

	public MessageSyncPhase()
	{
	}

	public MessageSyncPhase(int p)
	{
		phase = p;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return NetworkHandlerFTBAM.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeByte(phase);
	}

	@Override
	public void readData(DataIn data)
	{
		phase = data.readUnsignedByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBAcademyMod.setTutorialPhase(Minecraft.getMinecraft().player, phase);
	}
}