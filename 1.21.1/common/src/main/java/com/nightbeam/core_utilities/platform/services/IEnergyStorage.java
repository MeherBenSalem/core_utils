package com.nightbeam.core_utilities.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;

import org.jetbrains.annotations.Nullable;

public interface IEnergyStorage {
	boolean canExtractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side);

	boolean canReceiveEnergy(LevelAccessor level, BlockPos pos, Direction side);

	int getEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side);

	int getMaxEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side);

	int receiveEnergySimulate(LevelAccessor level, BlockPos pos, Direction side, int amount);

	int receiveEnergy(LevelAccessor level, BlockPos pos, Direction side, int amount);

	int extractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side, int amount);
}
