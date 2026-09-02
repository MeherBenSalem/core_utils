package com.nightbeam.core_utilities.procedures;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class FloatingBlockOnBlockRightClickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return;
		}
		if ((entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY).getItem() == CoreUtilitiesModBlocks.FLOATING_BLOCK.get().asItem()) {
			world.setBlock(BlockPos.containing(x, y, z), CoreUtilitiesModBlocks.FLOATING_BLOCK.get().defaultBlockState(), 3);
			if (entity instanceof Player player) {
				ItemStack toRemove = new ItemStack(CoreUtilitiesModBlocks.FLOATING_BLOCK.get());
				player.getInventory().clearOrCountMatchingItems(p -> toRemove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
			}
		}
	}
}
