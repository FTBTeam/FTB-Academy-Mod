package com.feed_the_beast.mods.ftbacademymod.kubejs;

import dev.latvian.kubejs.documentation.DocumentationEvent;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptsUnloadedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSIntegration
{
	public static final Map<String, DetectorEntry> DETECTORS = new HashMap<>();

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(KubeJSIntegration.class);
	}

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

	@SubscribeEvent
	public static void registerDocumentation(DocumentationEvent event)
	{
		event.registerEvent("ftbacademy.school.started", SchoolStartedEventJS.class).serverOnly();
		event.registerEvent("ftbacademy.school.ended", SchoolEndedEventJS.class).serverOnly();
	}
}