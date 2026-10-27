package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.platform.services.IItemTransfer;
import com.nightbeam.core_utilities.transfer.ContainerItemAccess;
import com.nightbeam.core_utilities.transfer.ItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class ForgeItemTransfer implements IItemTransfer {
	@Override
	@Nullable
	public ItemAccess find(Level level, BlockPos pos, @Nullable Direction side) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity == null) {
			return null;
		}
		IItemHandler handler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve().orElse(null);
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
		if (entity instanceof Container container) {
			return new ContainerItemAccess(container, side);
		}
		return null;
	}
}
