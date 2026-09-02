package com.nightbeam.core_utilities.block.entity;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class CableEBlockEntity extends EnergyBlockEntityBase {
	public CableEBlockEntity(BlockPos position, BlockState state) {
		super(CoreUtilitiesModBlockEntities.CABLE_E.get(), position, state, 64, 64, 64);
	}

	@Override
	protected Component getEnergyBlockName() {
		return Component.literal("Cable E");
	}
}
