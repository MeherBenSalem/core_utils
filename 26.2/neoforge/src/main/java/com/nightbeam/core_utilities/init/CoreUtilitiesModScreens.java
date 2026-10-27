package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.client.gui.BigBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.MediumBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.SmallBackpackGUIScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = com.nightbeam.core_utilities.CoreUtilitiesMod.MOD_ID, value = Dist.CLIENT)
public class CoreUtilitiesModScreens {
	@SubscribeEvent
	public static void register(RegisterMenuScreensEvent event) {
		event.register(CoreUtilitiesModMenusForge.SMALL_BACKPACK_GUI.get(), SmallBackpackGUIScreen::new);
		event.register(CoreUtilitiesModMenusForge.BIG_BACKPACK_GUI.get(), BigBackpackGUIScreen::new);
		event.register(CoreUtilitiesModMenusForge.MEDIUM_BACKPACK_GUI.get(), MediumBackpackGUIScreen::new);
		for (com.nightbeam.core_utilities.machine.MachineSpec spec : com.nightbeam.core_utilities.machine.MachineSpecs.ALL) {
			event.register(spec.menuType(), com.nightbeam.core_utilities.client.MachineScreen::new);
		}
	}
}
