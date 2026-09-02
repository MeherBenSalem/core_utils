package com.nightbeam.core_utilities.util;

import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class BlockEntityData {
	private BlockEntityData() {
	}

	public static CompoundTag get(BlockEntity blockEntity) {
		return Services.PLATFORM.getBlockEntityData(blockEntity);
	}

	public static double getDouble(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			return get(blockEntity).getDouble(tag);
		}
		return -1;
	}

	public static boolean getBoolean(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			return get(blockEntity).getBoolean(tag);
		}
		return false;
	}

	public static void putDouble(BlockEntity blockEntity, String tag, double value) {
		get(blockEntity).putDouble(tag, value);
	}
}
