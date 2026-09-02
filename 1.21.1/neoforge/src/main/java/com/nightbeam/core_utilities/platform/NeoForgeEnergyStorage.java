package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.block.entity.EnergyBlockEntityBase;
import com.nightbeam.core_utilities.platform.services.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class NeoForgeEnergyStorage implements IEnergyStorage {
	@Nullable
	private static net.neoforged.neoforge.energy.IEnergyStorage capability(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		if (!(level instanceof Level concrete)) {
			return null;
		}
		return concrete.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
	}

	@Override
	public boolean canExtractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().canExtract();
		}
		var storage = capability(level, pos, side);
		return storage != null && storage.canExtract();
	}

	@Override
	public boolean canReceiveEnergy(LevelAccessor level, BlockPos pos, Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().canReceive();
		}
		var storage = capability(level, pos, side);
		return storage != null && storage.canReceive();
	}

	@Override
	public int getEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().getEnergyStored();
		}
		var storage = capability(level, pos, side);
		return storage != null ? storage.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().getMaxEnergyStored();
		}
		var storage = capability(level, pos, side);
		return storage != null ? storage.getMaxEnergyStored() : 0;
	}

	@Override
	public int receiveEnergySimulate(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.receiveEnergy(amount, true);
		}
		var storage = capability(level, pos, side);
		return storage != null ? storage.receiveEnergy(amount, true) : 0;
	}

	@Override
	public int receiveEnergy(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.receiveEnergy(amount, false);
		}
		var storage = capability(level, pos, side);
		return storage != null ? storage.receiveEnergy(amount, false) : 0;
	}

	@Override
	public int extractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.extractEnergy(amount, false);
		}
		var storage = capability(level, pos, side);
		return storage != null ? storage.extractEnergy(amount, false) : 0;
	}
}
