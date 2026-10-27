package com.nightbeam.core_utilities.platform.services;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public interface IEnergyStorage {
	/**
	 * Finds an energy storage at {@code pos} accessed through face {@code side} of that block.
	 * Works for Core Utilities blocks and for any block exposing the loader's energy API.
	 */
	@Nullable
	EnergyHandler find(Level level, BlockPos pos, @Nullable Direction side);
}
