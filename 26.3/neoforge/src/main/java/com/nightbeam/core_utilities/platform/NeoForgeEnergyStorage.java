package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.platform.services.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

/**
 * Bridges our {@link EnergyHandler} to the NeoForge 26.x transfer API ({@code transfer.energy.EnergyHandler}), which
 * replaced the old {@code IEnergyStorage} capability and works with transactions instead of a simulate flag.
 */
public class NeoForgeEnergyStorage implements IEnergyStorage {
	@Override
	@Nullable
	public EnergyHandler find(Level level, BlockPos pos, @Nullable Direction side) {
		if (level.getBlockEntity(pos) instanceof EnergyHandlerProvider provider) {
			return provider.getEnergyHandler(side);
		}
		net.neoforged.neoforge.transfer.energy.EnergyHandler storage = level.getCapability(Capabilities.Energy.BLOCK, pos, side);
		return storage == null ? null : wrap(storage);
	}

	/** Runs one operation in its own transaction, nested in the caller's one if a transaction is already open. */
	@SuppressWarnings("deprecation")
	private static int transfer(net.neoforged.neoforge.transfer.energy.EnergyHandler storage, int amount, boolean insert, boolean simulate) {
		if (amount <= 0) {
			return 0;
		}
		try (Transaction transaction = Transaction.open(Transaction.getCurrentOpenedTransaction())) {
			int moved = insert ? storage.insert(amount, transaction) : storage.extract(amount, transaction);
			if (!simulate) {
				transaction.commit();
			}
			return moved;
		}
	}

	public static EnergyHandler wrap(net.neoforged.neoforge.transfer.energy.EnergyHandler storage) {
		return new EnergyHandler() {
			@Override
			public int receiveEnergy(int amount, boolean simulate) {
				return transfer(storage, amount, true, simulate);
			}

			@Override
			public int extractEnergy(int amount, boolean simulate) {
				return transfer(storage, amount, false, simulate);
			}

			@Override
			public int getEnergyStored() {
				return storage.getAmountAsInt();
			}

			@Override
			public int getMaxEnergyStored() {
				return storage.getCapacityAsInt();
			}

			@Override
			public boolean canReceive() {
				return true;
			}

			@Override
			public boolean canExtract() {
				return true;
			}
		};
	}

	/**
	 * Exposes one of our handlers as a NeoForge energy capability. Our handlers are not transactional, so transfers
	 * are simulated while the transaction is open and applied for real when the root transaction commits.
	 */
	public static net.neoforged.neoforge.transfer.energy.EnergyHandler expose(EnergyHandler handler) {
		return new Exposed(handler);
	}

	private static final class Exposed extends SnapshotJournal<int[]> implements net.neoforged.neoforge.transfer.energy.EnergyHandler {
		private final EnergyHandler handler;
		private int pendingInsert;
		private int pendingExtract;

		Exposed(EnergyHandler handler) {
			this.handler = handler;
		}

		@Override
		public long getAmountAsLong() {
			return Math.max(0L, (long) handler.getEnergyStored() + pendingInsert - pendingExtract);
		}

		@Override
		public long getCapacityAsLong() {
			return handler.getMaxEnergyStored();
		}

		@Override
		public int insert(int amount, TransactionContext transaction) {
			if (amount <= 0 || !handler.canReceive()) {
				return 0;
			}
			int accepted = Math.max(0, Math.min(amount, handler.receiveEnergy(pendingInsert + amount, true) - pendingInsert));
			if (accepted > 0) {
				updateSnapshots(transaction);
				pendingInsert += accepted;
			}
			return accepted;
		}

		@Override
		public int extract(int amount, TransactionContext transaction) {
			if (amount <= 0 || !handler.canExtract()) {
				return 0;
			}
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
		protected void revertToSnapshot(int[] snapshot) {
			pendingInsert = snapshot[0];
			pendingExtract = snapshot[1];
		}

		@Override
		protected void onRootCommit(int[] originalState) {
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
	}
}
