package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.platform.services.IEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
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
				try (Transaction transaction = open()) {
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
				try (Transaction transaction = open()) {
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

	/** Opens a transaction nested in the caller's one when a transaction is already open on this thread. */
	private static Transaction open() {
		return Transaction.isOpen() ? Transaction.getCurrentUnsafe().openNested() : Transaction.openOuter();
	}

	/**
	 * Exposes one of our handlers through Team Reborn Energy, honouring Fabric transactions. Our handlers are not
	 * transactional, so transfers are simulated while the transaction is open and applied after the outer commit.
	 */
	public static team.reborn.energy.api.EnergyStorage expose(EnergyHandler handler) {
		return new Exposed(handler);
	}

	private static final class Exposed extends SnapshotParticipant<int[]> implements team.reborn.energy.api.EnergyStorage {
		private final EnergyHandler handler;
		private int pendingInsert;
		private int pendingExtract;

		Exposed(EnergyHandler handler) {
			this.handler = handler;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			int amount = (int) Math.min(Integer.MAX_VALUE - pendingInsert, maxAmount);
			int accepted = Math.max(0, Math.min(amount, handler.receiveEnergy(pendingInsert + amount, true) - pendingInsert));
			if (accepted > 0) {
				updateSnapshots(transaction);
				pendingInsert += accepted;
			}
			return accepted;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			int amount = (int) Math.min(Integer.MAX_VALUE - pendingExtract, maxAmount);
			int extracted = Math.max(0, Math.min(amount, handler.extractEnergy(pendingExtract + amount, true) - pendingExtract));
			if (extracted > 0) {
				updateSnapshots(transaction);
				pendingExtract += extracted;
			}
			return extracted;
		}

		@Override
		protected int[] createSnapshot() {
			return new int[] {pendingInsert, pendingExtract};
		}

		@Override
		protected void readSnapshot(int[] snapshot) {
			pendingInsert = snapshot[0];
			pendingExtract = snapshot[1];
		}

		@Override
		protected void onFinalCommit() {
			int insert = pendingInsert;
			int extract = pendingExtract;
			pendingInsert = 0;
			pendingExtract = 0;
			if (insert > 0) {
				handler.receiveEnergy(insert, false);
			}
			if (extract > 0) {
				handler.extractEnergy(extract, false);
			}
		}

		@Override
		public long getAmount() {
			return Math.max(0L, (long) handler.getEnergyStored() + pendingInsert - pendingExtract);
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
	}
}
