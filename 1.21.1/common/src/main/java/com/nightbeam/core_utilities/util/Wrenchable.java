package com.nightbeam.core_utilities.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Blocks that react to the Configuration Wrench. Sneak-using the wrench dismantles them instead. */
public interface Wrenchable {
	/** @return true when the click was handled */
	boolean onWrench(Level level, BlockPos pos, BlockState state, Player player, BlockHitResult hit);
}
