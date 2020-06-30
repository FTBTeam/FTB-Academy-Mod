package com.feed_the_beast.mods.ftbacademymod.kubejs;

import dev.latvian.kubejs.script.BindingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class KubeJSIntegration
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(KubeJSIntegration.class);
	}

	@SubscribeEvent
	public static void registerBindings(BindingsEvent event)
	{
		event.add("ftbacademy", new FTBAcademyModWrapper());
	}
}