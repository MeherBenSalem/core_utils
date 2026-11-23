package com.nightbeam.core_utilities.platform.services;

import com.nightbeam.core_utilities.transfer.ItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public interface IItemTransfer {
	/** Finds an item inventory at {@code pos} accessed through face {@code side} of that block. */
	@Nullable
	ItemAccess find(Level level, BlockPos pos, @Nullable Direction side);
}
