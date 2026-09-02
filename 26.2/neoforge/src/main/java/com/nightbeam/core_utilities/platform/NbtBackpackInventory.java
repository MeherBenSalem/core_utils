package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.platform.services.IPlatformHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public class NbtBackpackInventory implements IPlatformHelper.ItemStackHandlerAccess {
	private static final String INVENTORY_KEY = "Inventory";
	private final ItemStack stack;
	private final NonNullList<ItemStack> items;
	private final HolderLookup.Provider registries;

	public NbtBackpackInventory(ItemStack stack) {
		this(stack, 27);
	}

	public NbtBackpackInventory(ItemStack stack, int size) {
		this.stack = stack;
		this.items = NonNullList.withSize(size, ItemStack.EMPTY);
		this.registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		load();
	}

	private void load() {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.getCompound(INVENTORY_KEY).ifPresent(inventory ->
				ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, registries, inventory), items));
	}

	private void save() {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
		ContainerHelper.saveAllItems(output, items);
		CompoundTag inventory = output.buildResult();
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(INVENTORY_KEY, inventory));
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
		if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, insert)) {
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
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
		ContainerHelper.saveAllItems(output, items);
		return output.buildResult();
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, registries, tag), items);
		save();
	}
}
