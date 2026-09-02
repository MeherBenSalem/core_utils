package com.nightbeam.core_utilities.block.entity;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanel2BlockEntity extends EnergyBlockEntityBase {
	public SolarPanel2BlockEntity(BlockPos position, BlockState state) {
		super(CoreUtilitiesModBlockEntities.SOLAR_PANEL_2.get(), position, state, 64, 0, 64);
	}

	@Override
	protected Component getEnergyBlockName() {
		return Component.literal("Solar Panel 2");
	}
}
