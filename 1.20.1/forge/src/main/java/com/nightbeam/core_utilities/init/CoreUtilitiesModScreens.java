package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.client.gui.BigBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.MediumBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.SmallBackpackGUIScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = com.nightbeam.core_utilities.CoreUtilitiesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CoreUtilitiesModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(CoreUtilitiesModMenusForge.SMALL_BACKPACK_GUI.get(), SmallBackpackGUIScreen::new);
			MenuScreens.register(CoreUtilitiesModMenusForge.BIG_BACKPACK_GUI.get(), BigBackpackGUIScreen::new);
			MenuScreens.register(CoreUtilitiesModMenusForge.MEDIUM_BACKPACK_GUI.get(), MediumBackpackGUIScreen::new);
		});
	}
}
