package com.nightbeam.core_utilities.events;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;
import com.nightbeam.core_utilities.procedures.FloatingBlockOnBlockRightClickedProcedure;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public class FabricCoreUtilitiesModEvents {
	public static void register() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() == CoreUtilitiesModBlocks.FLOATING_BLOCK.get().asItem()) {
				BlockPos pos = player.blockPosition();
				FloatingBlockOnBlockRightClickedProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), player);
				return InteractionResultHolder.success(stack);
			}
			return InteractionResultHolder.pass(stack);
		});
	}
}
