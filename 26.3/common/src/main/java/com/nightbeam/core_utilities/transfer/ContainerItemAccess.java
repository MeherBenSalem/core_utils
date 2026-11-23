package com.nightbeam.core_utilities.transfer;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** {@link ItemAccess} over a vanilla {@link Container}, honouring {@link WorldlyContainer} face rules. */
public class ContainerItemAccess implements ItemAccess {
	private final Container container;
	@Nullable
	private final Direction side;
	private final int[] slots;

	public ContainerItemAccess(Container container, @Nullable Direction side) {
		this.container = container;
		this.side = side;
		if (container instanceof WorldlyContainer worldly && side != null) {
			this.slots = worldly.getSlotsForFace(side);
		} else {
			this.slots = new int[container.getContainerSize()];
			for (int i = 0; i < slots.length; i++) {
				slots[i] = i;
			}
		}
	}

	@Override
	public int getSlots() {
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return container.getItem(slots[slot]);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int index = slots[slot];
		if (!container.canPlaceItem(index, stack)) {
			return stack;
		}
		if (container instanceof WorldlyContainer worldly && !worldly.canPlaceItemThroughFace(index, stack, side)) {
			return stack;
		}
		ItemStack existing = container.getItem(index);
		int limit = Math.min(container.getMaxStackSize(), stack.getMaxStackSize());
		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameComponents(existing, stack)) {
				return stack;
			}
			limit -= existing.getCount();
		}
		if (limit <= 0) {
			return stack;
		}
		int moved = Math.min(limit, stack.getCount());
		if (!simulate) {
			if (existing.isEmpty()) {
				container.setItem(index, stack.copyWithCount(moved));
			} else {
				existing.grow(moved);
			}
			container.setChanged();
		}
		return moved >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - moved);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		int index = slots[slot];
		ItemStack existing = container.getItem(index);
		if (existing.isEmpty() || amount <= 0) {
			return ItemStack.EMPTY;
		}
		if (container instanceof WorldlyContainer worldly && !worldly.canTakeItemThroughFace(index, existing, side)) {
			return ItemStack.EMPTY;
		}
		int moved = Math.min(amount, existing.getCount());
		ItemStack result = existing.copyWithCount(moved);
		if (!simulate) {
			container.removeItem(index, moved);
			container.setChanged();
		}
		return result;
	}
}
