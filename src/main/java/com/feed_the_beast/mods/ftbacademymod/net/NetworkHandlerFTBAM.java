package com.feed_the_beast.mods.ftbacademymod.net;

import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.ftbacademymod.FTBAcademyMod;

/**
 * @author LatvianModder
 */
public class NetworkHandlerFTBAM
{
	public static NetworkWrapper NET;

	public static void init()
	{
		NET = NetworkWrapper.newWrapper(FTBAcademyMod.MOD_ID);
		NET.register(new MessageSyncPhase());
	}
}