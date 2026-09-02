package com.nightbeam.core_utilities.inventory;

import com.nightbeam.core_utilities.platform.services.IPlatformHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackInventorySlot extends Slot {
	private final IPlatformHelper.ItemStackHandlerAccess handler;
	private final int slotIndex;

	public BackpackInventorySlot(IPlatformHelper.ItemStackHandlerAccess handler, int slotIndex, int x, int y) {
		super(new net.minecraft.world.SimpleContainer(1), 0, x, y);
		this.handler = handler;
		this.slotIndex = slotIndex;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getItem() {
		return handler.getStackInSlot(slotIndex);
	}

	@Override
	public void set(ItemStack stack) {
		handler.setStackInSlot(slotIndex, stack);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public ItemStack remove(int amount) {
		return handler.extractItem(slotIndex, amount, false);
	}

	@Override
	public boolean mayPickup(Player player) {
		return true;
	}
}
