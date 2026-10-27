package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.platform.services.IEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** Core Utilities energy on Fabric, bridged to Team Reborn Energy (the Fabric FE standard). */
public class FabricEnergyStorage implements IEnergyStorage {
	@Override
	@Nullable
	public EnergyHandler find(Level level, BlockPos pos, @Nullable Direction side) {
		if (level.getBlockEntity(pos) instanceof EnergyHandlerProvider provider) {
			return provider.getEnergyHandler(side);
		}
		team.reborn.energy.api.EnergyStorage storage = team.reborn.energy.api.EnergyStorage.SIDED.find(level, pos, side);
		return storage == null ? null : wrap(storage);
	}

	private static EnergyHandler wrap(team.reborn.energy.api.EnergyStorage storage) {
		return new EnergyHandler() {
			@Override
			public int receiveEnergy(int amount, boolean simulate) {
				if (!storage.supportsInsertion()) {
					return 0;
				}
				try (Transaction transaction = Transaction.openOuter()) {
					long inserted = storage.insert(amount, transaction);
					if (!simulate) {
						transaction.commit();
					}
					return (int) inserted;
				}
			}

			@Override
			public int extractEnergy(int amount, boolean simulate) {
				if (!storage.supportsExtraction()) {
					return 0;
				}
				try (Transaction transaction = Transaction.openOuter()) {
					long extracted = storage.extract(amount, transaction);
					if (!simulate) {
						transaction.commit();
					}
					return (int) extracted;
				}
			}

			@Override
			public int getEnergyStored() {
				return (int) Math.min(Integer.MAX_VALUE, storage.getAmount());
			}

			@Override
			public int getMaxEnergyStored() {
				return (int) Math.min(Integer.MAX_VALUE, storage.getCapacity());
			}

			@Override
			public boolean canReceive() {
				return storage.supportsInsertion();
			}

			@Override
			public boolean canExtract() {
				return storage.supportsExtraction();
			}
		};
	}

	/** Exposes one of our handlers through Team Reborn Energy, honouring Fabric transactions. */
	public static team.reborn.energy.api.EnergyStorage expose(EnergyHandler handler) {
		return new team.reborn.energy.api.EnergyStorage() {
			@Override
			public long insert(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
				int amount = handler.receiveEnergy((int) Math.min(Integer.MAX_VALUE, maxAmount), true);
				if (amount > 0) {
					transaction.addCloseCallback((context, result) -> {
						if (result.wasCommitted()) {
							handler.receiveEnergy(amount, false);
						}
					});
				}
				return amount;
			}

			@Override
			public long extract(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
				int amount = handler.extractEnergy((int) Math.min(Integer.MAX_VALUE, maxAmount), true);
				if (amount > 0) {
					transaction.addCloseCallback((context, result) -> {
						if (result.wasCommitted()) {
							handler.extractEnergy(amount, false);
						}
					});
				}
				return amount;
			}

			@Override
			public long getAmount() {
				return handler.getEnergyStored();
			}

			@Override
			public long getCapacity() {
				return handler.getMaxEnergyStored();
			}

			@Override
			public boolean supportsInsertion() {
				return handler.canReceive();
			}

			@Override
			public boolean supportsExtraction() {
				return handler.canExtract();
			}
		};
	}
}
