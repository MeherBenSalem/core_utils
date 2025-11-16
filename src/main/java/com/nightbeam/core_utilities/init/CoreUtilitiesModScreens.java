/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.nightbeam.core_utilities.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import com.nightbeam.core_utilities.client.gui.SmallBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.MediumBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.BigBackpackGUIScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CoreUtilitiesModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(CoreUtilitiesModMenus.SMALL_BACKPACK_GUI.get(), SmallBackpackGUIScreen::new);
			MenuScreens.register(CoreUtilitiesModMenus.BIG_BACKPACK_GUI.get(), BigBackpackGUIScreen::new);
			MenuScreens.register(CoreUtilitiesModMenus.MEDIUM_BACKPACK_GUI.get(), MediumBackpackGUIScreen::new);
		});
	}

	public interface ScreenAccessor {
		void updateMenuState(int elementType, String name, Object elementState);
	}
}