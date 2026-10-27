package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.platform.services.IItemTransfer;
import com.nightbeam.core_utilities.transfer.ContainerItemAccess;
import com.nightbeam.core_utilities.transfer.ItemAccess;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FabricItemTransfer implements IItemTransfer {
	@Override
	@Nullable
	public ItemAccess find(Level level, BlockPos pos, @Nullable Direction side) {
		if (level.getBlockEntity(pos) instanceof Container container) {
			return new ContainerItemAccess(container, side);
		}
		Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, side);
		return storage == null ? null : new StorageAccess(storage);
	}

	/** Slot view over a Transfer API storage: one slot per non-empty view, plus one free insertion slot. */
	private static class StorageAccess implements ItemAccess {
		private final Storage<ItemVariant> storage;
		private final List<StorageView<ItemVariant>> views = new ArrayList<>();

		StorageAccess(Storage<ItemVariant> storage) {
			this.storage = storage;
			for (StorageView<ItemVariant> view : storage) {
				if (!view.isResourceBlank() && view.getAmount() > 0) {
					views.add(view);
				}
			}
		}

		@Override
		public int getSlots() {
			return views.size() + 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			if (slot >= views.size()) {
				return ItemStack.EMPTY;
			}
			StorageView<ItemVariant> view = views.get(slot);
			return view.getResource().toStack((int) Math.min(Integer.MAX_VALUE, view.getAmount()));
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.isEmpty() || !storage.supportsInsertion()) {
				return stack;
			}
			try (Transaction transaction = Transaction.openOuter()) {
				long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
				if (!simulate) {
					transaction.commit();
				}
				return inserted >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - (int) inserted);
			}
		}

		@Override
		public ItemStack insert(ItemStack stack, boolean simulate) {
			return insertItem(0, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot >= views.size() || amount <= 0 || !storage.supportsExtraction()) {
				return ItemStack.EMPTY;
			}
			ItemVariant resource = views.get(slot).getResource();
			try (Transaction transaction = Transaction.openOuter()) {
				long extracted = storage.extract(resource, Math.min(amount, resource.getItem().getMaxStackSize()), transaction);
				if (!simulate) {
					transaction.commit();
				}
				return extracted <= 0 ? ItemStack.EMPTY : resource.toStack((int) extracted);
			}
		}
	}
}
