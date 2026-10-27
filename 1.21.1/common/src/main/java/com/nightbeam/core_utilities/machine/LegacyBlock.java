package com.nightbeam.core_utilities.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * Placeholder for block ids used by older versions (the per-shape cable blocks and per-weather solar panels).
 * Existing blocks in old worlds turn into their modern replacement on their next tick.
 */
public class LegacyBlock extends Block {
	private final Supplier<BlockState> replacement;

	public LegacyBlock(Supplier<BlockState> replacement) {
		super(BlockBehaviour.Properties.of().strength(0.5f).noOcclusion().randomTicks());
		this.replacement = replacement;
	}

	public BlockState getReplacement() {
		return replacement.get();
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(state, level, pos, oldState, moving);
		level.scheduleTick(pos, this, 1);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		convert(level, pos);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		convert(level, pos);
	}

	private void convert(ServerLevel level, BlockPos pos) {
		BlockState target = replacement.get();
		if (target.getBlock() instanceof com.nightbeam.core_utilities.conduit.ConduitBlock conduit) {
			target = conduit.refreshConnections(level, pos, target);
		}
		level.setBlock(pos, target, Block.UPDATE_ALL);
	}
}
