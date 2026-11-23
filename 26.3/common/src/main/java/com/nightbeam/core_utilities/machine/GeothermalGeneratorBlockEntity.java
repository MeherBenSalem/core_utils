package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

/** Generates FE from every lava source block touching it. The lava is not consumed. */
public class GeothermalGeneratorBlockEntity extends MachineBlockEntity {
	private int lavaFaces;

	public GeothermalGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec) {
		super(type, pos, state, spec, 40_000, 0, 2_000);
	}

	@Override
	protected boolean work() {
		if (level.getGameTime() % 20 == Math.floorMod(worldPosition.hashCode(), 20) || maxProgress == 0) {
			lavaFaces = 0;
			for (Direction direction : Direction.values()) {
				FluidState fluid = level.getFluidState(worldPosition.relative(direction));
				if (fluid.is(FluidTags.LAVA) && fluid.isSource()) {
					lavaFaces++;
				}
			}
			maxProgress = 6;
			extra = lavaFaces;
		}
		progress = lavaFaces;
		boolean running = false;
		if (lavaFaces > 0) {
			lastUsage = energy.generate(lavaFaces * CoreUtilitiesConfig.get().geothermalRatePerLava);
			running = lastUsage > 0;
			if (running) {
				setChanged();
			}
		}
		pushEnergy(2_000, d -> !level.getFluidState(worldPosition.relative(d)).is(FluidTags.LAVA));
		return running;
	}

	@Override
	protected boolean canReceiveEnergyFrom(@Nullable Direction side) {
		return false;
	}
}
