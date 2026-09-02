package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.platform.services.IPlatformHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public class NbtBackpackInventory implements IPlatformHelper.ItemStackHandlerAccess {
	private static final String INVENTORY_KEY = "Inventory";
	private final ItemStack stack;
	private final NonNullList<ItemStack> items;

	public NbtBackpackInventory(ItemStack stack) {
		this(stack, 27);
	}

	public NbtBackpackInventory(ItemStack stack, int size) {
		this.stack = stack;
		this.items = NonNullList.withSize(size, ItemStack.EMPTY);
		load();
	}

	private void load() {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(INVENTORY_KEY, Tag.TAG_COMPOUND)) {
			ContainerHelper.loadAllItems(tag.getCompound(INVENTORY_KEY), items);
		}
	}

	private void save() {
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag inventory = new CompoundTag();
		ContainerHelper.saveAllItems(inventory, items);
		tag.put(INVENTORY_KEY, inventory);
	}

	@Override
	public int getSlots() {
		return items.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return items.get(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		items.set(slot, stack);
		save();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack insert, boolean simulate) {
		ItemStack existing = items.get(slot);
		if (!existing.isEmpty() && !ItemStack.isSameItemSameTags(existing, insert)) {
			return insert;
		}
		int max = Math.min(insert.getMaxStackSize(), 64);
		int space = max - existing.getCount();
		int moved = Math.min(space, insert.getCount());
		if (moved <= 0) {
			return insert;
		}
		if (!simulate) {
			ItemStack result = insert.copy();
			result.shrink(moved);
			ItemStack placed = insert.copy();
			placed.setCount(existing.getCount() + moved);
			items.set(slot, placed);
			save();
			return result;
		}
		ItemStack result = insert.copy();
		result.shrink(moved);
		return result;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack existing = items.get(slot);
		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int moved = Math.min(amount, existing.getCount());
		ItemStack extracted = existing.copy();
		extracted.setCount(moved);
		if (!simulate) {
			existing.shrink(moved);
			items.set(slot, existing.isEmpty() ? ItemStack.EMPTY : existing);
			save();
		}
		return extracted;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, items);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		if (tag.contains("Items", Tag.TAG_LIST)) {
			ContainerHelper.loadAllItems(tag, items);
			save();
		}
	}
}
