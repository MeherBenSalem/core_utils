package com.nightbeam.core_utilities.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Breaks the block in front of it whenever there is one (cobblestone generators, tree farms...). */
public class BlockBreakerBlockEntity extends AreaMachineBlockEntity {
	public BlockBreakerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec) {
		super(type, pos, state, spec, 20_000);
	}

	@Override
	protected boolean work() {
		maxProgress = scaledTime(20);
		int usage = scaledUsage(60);
		BlockPos target = worldPosition.relative(getFacing());
		BlockState state = level.getBlockState(target);
		if (!canBreak(target, state) || !hasOutputSpace() || energy.getEnergyStored() < usage) {
			progress = 0;
			return false;
		}
		progress++;
		if (progress >= maxProgress) {
			progress = 0;
			energy.consume(usage);
			lastUsage = usage;
			store(breakBlock(target, state));
		}
		return true;
	}
}
