package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.platform.services.IItemTransfer;
import com.nightbeam.core_utilities.transfer.ContainerItemAccess;
import com.nightbeam.core_utilities.transfer.ItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

/** Item access through the NeoForge 26.x transfer API ({@code ResourceHandler<ItemResource>}). */
public class NeoForgeItemTransfer implements IItemTransfer {
	@Override
	@Nullable
	public ItemAccess find(Level level, BlockPos pos, @Nullable Direction side) {
		ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, side);
		if (handler != null) {
			return new ItemAccess() {
				@Override
				public int getSlots() {
					return handler.size();
				}

				@Override
				public ItemStack getStackInSlot(int slot) {
					ItemResource resource = handler.getResource(slot);
					return resource.isEmpty() ? ItemStack.EMPTY : resource.toStack(handler.getAmountAsInt(slot));
				}

				@Override
				@SuppressWarnings("deprecation")
				public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
					if (stack.isEmpty()) {
						return ItemStack.EMPTY;
					}
					try (Transaction transaction = Transaction.open(Transaction.getCurrentOpenedTransaction())) {
						int inserted = handler.insert(slot, ItemResource.of(stack), stack.getCount(), transaction);
						if (!simulate) {
							transaction.commit();
						}
						return inserted >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - inserted);
					}
				}

				@Override
				@SuppressWarnings("deprecation")
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					ItemResource resource = handler.getResource(slot);
					if (resource.isEmpty() || amount <= 0) {
						return ItemStack.EMPTY;
					}
					try (Transaction transaction = Transaction.open(Transaction.getCurrentOpenedTransaction())) {
						int extracted = handler.extract(slot, resource, amount, transaction);
						if (!simulate) {
							transaction.commit();
						}
						return extracted <= 0 ? ItemStack.EMPTY : resource.toStack(extracted);
					}
				}
			};
		}
		if (level.getBlockEntity(pos) instanceof Container container) {
			return new ContainerItemAccess(container, side);
		}
		return null;
	}
}
