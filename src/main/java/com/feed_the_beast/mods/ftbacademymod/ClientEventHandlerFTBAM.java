package com.feed_the_beast.mods.ftbacademymod;

import com.feed_the_beast.ftblib.events.SidebarButtonCreatedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;
import java.util.function.BooleanSupplier;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBAcademyMod.MOD_ID, value = Side.CLIENT)
public class ClientEventHandlerFTBAM
{
	private static final BooleanSupplier BUTTON_VISIBILITY = () -> !FTBAcademyMod.isInTutorial(Minecraft.getMinecraft().player);
	private static final HashSet<ResourceLocation> EXCLUDED_BUTTONS = new HashSet<>();

	static
	{
		EXCLUDED_BUTTONS.add(new ResourceLocation("ftbquests:quests"));
		EXCLUDED_BUTTONS.add(new ResourceLocation("ftbguides:guides"));
		EXCLUDED_BUTTONS.add(new ResourceLocation("ftbtutorialmod:tutorials"));
	}

	@SubscribeEvent
	public static void onSidebarButtonCreated(SidebarButtonCreatedEvent event)
	{
		if (!EXCLUDED_BUTTONS.contains(event.getButton().id))
		{
			event.getButton().addVisibilityCondition(BUTTON_VISIBILITY);
		}
	}
}