package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import com.nightbeam.core_utilities.init.CoreUtilitiesModItems;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;

public class FilterSpiderOnBlockRightClickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CoreUtilitiesModItems.CONFIGURATION_WRENCH.get()) {
			world.setBlock(BlockPos.containing(x, y, z), CoreUtilitiesModBlocks.DENY_FILTER_SPIDER.get().defaultBlockState(), 3);
		}
	}
}
