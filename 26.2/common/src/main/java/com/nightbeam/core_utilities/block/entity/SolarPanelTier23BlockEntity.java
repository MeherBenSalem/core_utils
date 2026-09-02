package com.nightbeam.core_utilities.block.entity;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelTier23BlockEntity extends EnergyBlockEntityBase {
	public SolarPanelTier23BlockEntity(BlockPos position, BlockState state) {
		super(CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_23.get(), position, state, 128, 0, 128);
	}

	@Override
	protected Component getEnergyBlockName() {
		return Component.literal("Solar Panel Tier 23");
	}
}
