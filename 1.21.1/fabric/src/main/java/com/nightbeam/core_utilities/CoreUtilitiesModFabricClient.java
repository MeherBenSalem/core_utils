package com.nightbeam.core_utilities;

import com.nightbeam.core_utilities.client.gui.BigBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.MediumBackpackGUIScreen;
import com.nightbeam.core_utilities.client.gui.SmallBackpackGUIScreen;
import com.nightbeam.core_utilities.init.CoreUtilitiesModMenusFabric;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class CoreUtilitiesModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		com.nightbeam.core_utilities.events.FabricCoreUtilitiesModEvents.registerClient();
		MenuScreens.register(CoreUtilitiesModMenusFabric.getSmallMenu(), SmallBackpackGUIScreen::new);
		MenuScreens.register(CoreUtilitiesModMenusFabric.getMediumMenu(), MediumBackpackGUIScreen::new);
		MenuScreens.register(CoreUtilitiesModMenusFabric.getBigMenu(), BigBackpackGUIScreen::new);
	}
}
