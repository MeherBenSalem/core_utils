package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.platform.services.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class NeoForgeEnergyStorage implements IEnergyStorage {
	@Override
	@Nullable
	public EnergyHandler find(Level level, BlockPos pos, @Nullable Direction side) {
		if (level.getBlockEntity(pos) instanceof EnergyHandlerProvider provider) {
			return provider.getEnergyHandler(side);
		}
		net.neoforged.neoforge.energy.IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
		return storage == null ? null : wrap(storage);
	}

	public static EnergyHandler wrap(net.neoforged.neoforge.energy.IEnergyStorage storage) {
		return new EnergyHandler() {
			@Override
			public int receiveEnergy(int amount, boolean simulate) {
				return storage.receiveEnergy(amount, simulate);
			}

			@Override
			public int extractEnergy(int amount, boolean simulate) {
				return storage.extractEnergy(amount, simulate);
			}

			@Override
			public int getEnergyStored() {
				return storage.getEnergyStored();
			}

			@Override
			public int getMaxEnergyStored() {
				return storage.getMaxEnergyStored();
			}

			@Override
			public boolean canReceive() {
				return storage.canReceive();
			}

			@Override
			public boolean canExtract() {
				return storage.canExtract();
			}
		};
	}

	/** Exposes one of our handlers as a NeoForge energy capability. */
	public static net.neoforged.neoforge.energy.IEnergyStorage expose(EnergyHandler handler) {
		return new net.neoforged.neoforge.energy.IEnergyStorage() {
			@Override
			public int receiveEnergy(int amount, boolean simulate) {
				return handler.receiveEnergy(amount, simulate);
			}

			@Override
			public int extractEnergy(int amount, boolean simulate) {
				return handler.extractEnergy(amount, simulate);
			}

			@Override
			public int getEnergyStored() {
				return handler.getEnergyStored();
			}

			@Override
			public int getMaxEnergyStored() {
				return handler.getMaxEnergyStored();
			}

			@Override
			public boolean canExtract() {
				return handler.canExtract();
			}

			@Override
			public boolean canReceive() {
				return handler.canReceive();
			}
		};
	}
}
