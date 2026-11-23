package com.nightbeam.core_utilities.transfer;

import net.minecraft.world.item.ItemStack;

/** Loader independent slot based view of an inventory (NeoForge/Forge item handlers, Fabric storages, vanilla containers). */
public interface ItemAccess {
	int getSlots();

	ItemStack getStackInSlot(int slot);

	/** @return the part of {@code stack} that did not fit */
	ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

	ItemStack extractItem(int slot, int amount, boolean simulate);

	/** Inserts into any slot, merging with existing stacks first. */
	default ItemStack insert(ItemStack stack, boolean simulate) {
		ItemStack remaining = stack.copy();
		for (int i = 0; i < getSlots() && !remaining.isEmpty(); i++) {
			if (!getStackInSlot(i).isEmpty()) {
				remaining = insertItem(i, remaining, simulate);
			}
		}
		for (int i = 0; i < getSlots() && !remaining.isEmpty(); i++) {
			if (getStackInSlot(i).isEmpty()) {
				remaining = insertItem(i, remaining, simulate);
			}
		}
		return remaining;
	}
}
