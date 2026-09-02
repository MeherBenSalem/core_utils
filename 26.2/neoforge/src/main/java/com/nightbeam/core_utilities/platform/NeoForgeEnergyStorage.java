package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.block.entity.EnergyBlockEntityBase;
import com.nightbeam.core_utilities.platform.services.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class NeoForgeEnergyStorage implements IEnergyStorage {
	@Nullable
	private static EnergyHandler capability(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		if (!(level instanceof Level concrete)) {
			return null;
		}
		return concrete.getCapability(Capabilities.Energy.BLOCK, pos, side);
	}

	@Override
	public boolean canExtractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().canExtract();
		}
		EnergyHandler storage = capability(level, pos, side);
		return storage != null && storage.getAmountAsInt() > 0;
	}

	@Override
	public boolean canReceiveEnergy(LevelAccessor level, BlockPos pos, Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().canReceive();
		}
		EnergyHandler storage = capability(level, pos, side);
		return storage != null && storage.getAmountAsInt() < storage.getCapacityAsInt();
	}

	@Override
	public int getEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().getEnergyStored();
		}
		EnergyHandler storage = capability(level, pos, side);
		return storage != null ? storage.getAmountAsInt() : 0;
	}

	@Override
	public int getMaxEnergyStored(LevelAccessor level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.getEnergyBuffer().getMaxEnergyStored();
		}
		EnergyHandler storage = capability(level, pos, side);
		return storage != null ? storage.getCapacityAsInt() : 0;
	}

	@Override
	public int receiveEnergySimulate(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.receiveEnergy(amount, true);
		}
		EnergyHandler storage = capability(level, pos, side);
		if (storage == null) {
			return 0;
		}
		try (Transaction tx = Transaction.openRoot()) {
			return storage.insert(amount, tx);
		}
	}

	@Override
	public int receiveEnergy(LevelAccessor level, BlockPos pos, Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.receiveEnergy(amount, false);
		}
		EnergyHandler storage = capability(level, pos, side);
		if (storage == null) {
			return 0;
		}
		try (Transaction tx = Transaction.openRoot()) {
			int received = storage.insert(amount, tx);
			tx.commit();
			return received;
		}
	}

	@Override
	public int extractEnergy(LevelAccessor level, BlockPos pos, @Nullable Direction side, int amount) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof EnergyBlockEntityBase energyBlock) {
			return energyBlock.extractEnergy(amount, false);
		}
		EnergyHandler storage = capability(level, pos, side);
		if (storage == null) {
			return 0;
		}
		try (Transaction tx = Transaction.openRoot()) {
			int extracted = storage.extract(amount, tx);
			tx.commit();
			return extracted;
		}
	}
}