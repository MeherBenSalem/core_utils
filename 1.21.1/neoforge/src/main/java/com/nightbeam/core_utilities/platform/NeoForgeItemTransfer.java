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
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class NeoForgeItemTransfer implements IItemTransfer {
	@Override
	@Nullable
	public ItemAccess find(Level level, BlockPos pos, @Nullable Direction side) {
		IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
		if (handler != null) {
			return new ItemAccess() {
				@Override
				public int getSlots() {
					return handler.getSlots();
				}

				@Override
				public ItemStack getStackInSlot(int slot) {
					return handler.getStackInSlot(slot);
				}

				@Override
				public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
					return handler.insertItem(slot, stack, simulate);
				}

				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					return handler.extractItem(slot, amount, simulate);
				}
			};
		}
		if (level.getBlockEntity(pos) instanceof Container container) {
			return new ContainerItemAccess(container, side);
		}
		return null;
	}
}
