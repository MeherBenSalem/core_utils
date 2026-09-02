package com.nightbeam.core_utilities;

import com.nightbeam.core_utilities.events.FabricCoreUtilitiesModEvents;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntitiesFabric;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocksFabric;
import com.nightbeam.core_utilities.init.CoreUtilitiesModItemsFabric;
import com.nightbeam.core_utilities.init.CoreUtilitiesModMenusFabric;
import com.nightbeam.core_utilities.init.CoreUtilitiesModTabsFabric;
import com.nightbeam.core_utilities.network.CoreUtilitiesModNetworkFabric;
import com.nightbeam.core_utilities.procedures.ForgeFlowTimerProcedure;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class CoreUtilitiesModFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		CoreUtilitiesModBlocksFabric.register();
		CoreUtilitiesModItemsFabric.register();
		CoreUtilitiesModBlockEntitiesFabric.register();
		CoreUtilitiesModMenusFabric.register();
		CoreUtilitiesModTabsFabric.register();
		CoreUtilitiesModNetworkFabric.register();
		FabricCoreUtilitiesModEvents.register();
		ServerTickEvents.END_WORLD_TICK.register(level -> ForgeFlowTimerProcedure.execute(level));
	}
}
