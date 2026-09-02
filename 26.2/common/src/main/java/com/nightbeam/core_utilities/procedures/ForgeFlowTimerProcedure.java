package com.nightbeam.core_utilities.procedures;

import com.nightbeam.core_utilities.network.CoreUtilitiesModVariables;
import net.minecraft.world.level.LevelAccessor;

public class ForgeFlowTimerProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide()) {
			CoreUtilitiesModVariables.MapVariables vars = CoreUtilitiesModVariables.MapVariables.get(world);
			if (vars.forgeFlow > 0) {
				vars.forgeFlow = vars.forgeFlow - 1;
			} else {
				vars.forgeFlow = 300;
			}
			vars.syncData(world);
		}
	}
}
