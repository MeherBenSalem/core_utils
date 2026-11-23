package com.nightbeam.core_utilities.client;

import net.minecraft.client.Minecraft;

/** Client-only entry points called from common code behind a side check. */
public final class ClientHooks {
	private ClientHooks() {
	}

	public static void openGuide() {
		Minecraft.getInstance().gui.setScreen(new GuideScreen());
	}
}
