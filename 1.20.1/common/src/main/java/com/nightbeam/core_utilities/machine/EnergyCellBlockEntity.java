package com.nightbeam.core_utilities.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Tiered FE storage. Faces set to input accept energy, faces set to output push energy every tick. Keeps its
 * charge when broken.
 */
public class EnergyCellBlockEntity extends MachineBlockEntity {
	private final CellTier tier;

	public EnergyCellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec, CellTier tier) {
		super(type, pos, state, spec, tier.capacity(), tier.rate(), tier.rate());
		this.tier = tier;
		if (tier == CellTier.CREATIVE) {
			energy.setEnergyStored(Integer.MAX_VALUE);
		}
	}

	public CellTier getTier() {
		return tier;
	}

	@Override
	protected void defaultSides(SideMode[] sides) {
		java.util.Arrays.fill(sides, SideMode.INPUT);
		sides[RelativeSide.FRONT.ordinal()] = SideMode.OUTPUT;
	}

	@Override
	protected boolean work() {
		if (tier == CellTier.CREATIVE) {
			energy.setEnergyStored(Integer.MAX_VALUE);
		}
		int before = energy.getEnergyStored();
		pushEnergy(tier.rate(), d -> getSideMode(d).allowsOutput());
		lastUsage = before - energy.getEnergyStored();
		if (tier == CellTier.CREATIVE) {
			energy.setEnergyStored(Integer.MAX_VALUE);
		}
		progress = (int) (1000L * energy.getEnergyStored() / Math.max(1, energy.getMaxEnergyStored()));
		maxProgress = 1000;
		return lastUsage > 0;
	}

	@Override
	protected boolean canReceiveEnergyFrom(@Nullable Direction side) {
		return side == null || getSideMode(side).allowsInput();
	}

	@Override
	protected boolean canExtractEnergyFrom(@Nullable Direction side) {
		return side == null || getSideMode(side).allowsOutput();
	}

	// The charge travels with the item as BlockEntityTag.Energy (loot table copy_nbt); BlockItem applies it through load().

	@Override
	protected void loadExtra(CompoundTag tag) {
		if (tier == CellTier.CREATIVE) {
			energy.setEnergyStored(Integer.MAX_VALUE);
		}
	}
}
