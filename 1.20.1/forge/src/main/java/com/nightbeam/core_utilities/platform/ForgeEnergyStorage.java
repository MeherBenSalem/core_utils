package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.block.entity.EnergyBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;

public class ForgeEnergyStorage implements com.nightbeam.core_utilities.platform.services.IEnergyStorage {
	@Override
	public boolean canExtractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return side == null ? energyBlock.getEnergyBuffer().canExtract() : energyBlock.getEnergyBuffer().canExtract();
		}
		return getCapability(level, pos, side).map(net.minecraftforge.energy.IEnergyStorage::canExtract).orElse(false);
	}

	@Override
	public boolean canReceiveEnergy(LevelAccessor level, BlockPos pos, Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().canReceive();
		}
		return getCapability(level, pos, side).map(net.minecraftforge.energy.IEnergyStorage::canReceive).orElse(false);
	}

	@Override
	public int getEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().getEnergyStored();
		}
		return getCapability(level, pos, side).map(net.minecraftforge.energy.IEnergyStorage::getEnergyStored).orElse(0);
	}

	@Override
	public int getMaxEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().getMaxEnergyStored();
		}
		return getCapability(level, pos, side).map(net.minecraftforge.energy.IEnergyStorage::getMaxEnergyStored).orElse(0);
	}

	@Override
	public int receiveEnergySimulate(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.receiveEnergy(amount, true);
		}
		return getCapability(level, pos, side).map(cap -> cap.receiveEnergy(amount, true)).orElse(0);
	}

	@Override
	public int receiveEnergy(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.receiveEnergy(amount, false);
		}
		return getCapability(level, pos, side).map(cap -> cap.receiveEnergy(amount, false)).orElse(0);
	}

	@Override
	public int extractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.extractEnergy(amount, false);
		}
		return getCapability(level, pos, side).map(cap -> cap.extractEnergy(amount, false)).orElse(0);
	}

	private java.util.Optional<net.minecraftforge.energy.IEnergyStorage> getCapability(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity == null) {
			return java.util.Optional.empty();
		}
		return entity.getCapability(ForgeCapabilities.ENERGY, side).resolve();
	}
}


