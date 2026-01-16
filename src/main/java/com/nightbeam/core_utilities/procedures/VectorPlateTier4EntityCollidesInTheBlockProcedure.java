package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class VectorPlateTier4EntityCollidesInTheBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		VectorPlateWalksOnTheBlockProcedure.execute(world, x, y, z, blockstate, entity, 1);
	}
}