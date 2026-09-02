package com.nightbeam.core_utilities.block.entity;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class CableIBlockEntity extends EnergyBlockEntityBase {
	public CableIBlockEntity(BlockPos position, BlockState state) {
		super(CoreUtilitiesModBlockEntities.CABLE_I.get(), position, state, 64, 64, 64);
	}

	@Override
	protected Component getEnergyBlockName() {
		return Component.literal("Cable I");
	}
}
