package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.block.entity.EnergyBlockEntityBase;
import com.nightbeam.core_utilities.platform.services.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public class FabricEnergyStorage implements IEnergyStorage {
	@Nullable
	private static EnergyBlockEntityBase energyBlock(LevelAccessor level, BlockPos pos) {
		BlockEntity entity = level.getBlockEntity(pos);
		return entity instanceof EnergyBlockEntityBase energy ? energy : null;
	}

	@Override
	public boolean canExtractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		EnergyBlockEntityBase block = energyBlock(level, pos);
		return block != null && block.getEnergyBuffer().canExtract();
	}

	@Override
	public boolean canReceiveEnergy(LevelAccessor level, BlockPos pos, Direction side) {
		EnergyBlockEntityBase block = energyBlock(level, pos);
		return block != null && block.getEnergyBuffer().canReceive();
	}

	@Override
	public int getEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		EnergyBlockEntityBase block = energyBlock(level, pos);
		return block == null ? 0 : block.getEnergyBuffer().getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		EnergyBlockEntityBase block = energyBlock(level, pos);
		return block == null ? 0 : block.getEnergyBuffer().getMaxEnergyStored();
	}

	@Override
	public int receiveEnergySimulate(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		EnergyBlockEntityBase block = energyBlock(level, pos);
		return block == null ? 0 : block.receiveEnergy(amount, true);
	}

	@Override
	public int receiveEnergy(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		EnergyBlockEntityBase block = energyBlock(level, pos);
		return block == null ? 0 : block.receiveEnergy(amount, false);
	}

	@Override
	public int extractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side, int amount) {
		EnergyBlockEntityBase block = energyBlock(level, pos);
		return block == null ? 0 : block.extractEnergy(amount, false);
	}
}
