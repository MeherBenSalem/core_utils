package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.level.LevelAccessor;

public class CableUpdateTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (!world.isClientSide()) {
			CableModelUpdaterMainProcedure.execute(world, x, y, z);
			CableEnergyTransferLogic.execute(world, x, y, z);
		}
	}
}

