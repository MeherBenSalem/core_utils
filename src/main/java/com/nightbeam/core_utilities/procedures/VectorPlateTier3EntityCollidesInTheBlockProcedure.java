package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;

public class VectorPlateTier3EntityCollidesInTheBlockProcedure {
	public static void execute(BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		VectorPlateWalksOnTheBlockProcedure.execute(blockstate, entity, 0.5);
	}
}