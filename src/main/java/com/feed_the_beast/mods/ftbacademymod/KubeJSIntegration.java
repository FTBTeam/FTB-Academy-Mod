package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.mods.ftbacademymod.blocks.DetectorPredicate;
import com.feed_the_beast.mods.ftbacademymod.blocks.FTBAcademyModWrapper;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptsUnloadedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBAcademyMod.MOD_ID)
public class KubeJSIntegration
{
	public static final Map<String, DetectorPredicate> DETECTORS = new HashMap<>();

	@SubscribeEvent
	public static void scriptsUnloaded(ScriptsUnloadedEvent event)
	{
		DETECTORS.clear();
	}

	@SubscribeEvent
	public static void registerBindings(BindingsEvent event)
	{
		event.add("ftbacademy", new FTBAcademyModWrapper());
	}
}