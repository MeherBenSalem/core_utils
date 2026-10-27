package com.nightbeam.core_utilities;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;
import com.nightbeam.core_utilities.init.CoreUtilitiesModItems;
import com.nightbeam.core_utilities.init.CoreUtilitiesModMenus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreUtilitiesMod {
	public static final String MOD_ID = "core_utilities";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Registers all shared content. Called once by every loader's entry point. */
	public static void init() {
		CoreUtilitiesModBlocks.register();
		CoreUtilitiesModItems.register();
		CoreUtilitiesModBlockEntities.register();
		CoreUtilitiesModMenus.registerMachines();
	}
}
