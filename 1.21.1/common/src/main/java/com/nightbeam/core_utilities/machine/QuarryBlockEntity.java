package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mines a square area below itself layer by layer down to the bottom of the world. Leaves fluids, block entities
 * and deny-listed blocks alone. Range upgrades widen the area, speed upgrades mine faster.
 */
public class QuarryBlockEntity extends AreaMachineBlockEntity {
	private int cursor = -1;
	private int layer = Integer.MIN_VALUE;
	private int cooldown;
	private boolean finished;

	public QuarryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec) {
		super(type, pos, state, spec, 100_000);
	}

	@Override
	protected boolean work() {
		CoreUtilitiesConfig config = CoreUtilitiesConfig.get();
		int radius = radius(config.quarryBaseRadius, config.quarryMaxRadius);
		int side = radius * 2 + 1;
		if (layer == Integer.MIN_VALUE) {
			layer = worldPosition.getY() - 1;
			cursor = -1;
		}
		extra = layer;
		maxProgress = Math.max(1, worldPosition.getY() - level.getMinBuildHeight());
		progress = worldPosition.getY() - layer;
		if (finished || !hasOutputSpace()) {
			return false;
		}
		int usage = scaledUsage(config.quarryEnergyPerBlock);
		if (energy.getEnergyStored() < usage) {
			return false;
		}
		if (cooldown > 0) {
			cooldown--;
			return true;
		}
		int scanned = 0;
		while (scanned < 256) {
			cursor++;
			if (cursor >= side * side) {
				cursor = 0;
				layer--;
				if (layer < level.getMinBuildHeight()) {
					finished = true;
					setChanged();
					return false;
				}
			}
			BlockPos target = new BlockPos(worldPosition.getX() - radius + cursor % side, layer, worldPosition.getZ() - radius + cursor / side);
			scanned++;
			// leave the column under the quarry standing so the machine never undermines itself
			if (target.getX() == worldPosition.getX() && target.getZ() == worldPosition.getZ()) {
				continue;
			}
			if (!level.isLoaded(target)) {
				continue;
			}
			BlockState state = level.getBlockState(target);
			if (!canBreak(target, state)) {
				continue;
			}
			energy.consume(usage);
			lastUsage = usage;
			store(breakBlock(target, state));
			cooldown = scaledTime(10) - 1;
			setChanged();
			return true;
		}
		return true;
	}

	public void restart() {
		layer = Integer.MIN_VALUE;
		finished = false;
		setChanged();
	}

	@Override
	protected void loadExtra(CompoundTag tag, HolderLookup.Provider registries) {
		cursor = tag.getInt("Cursor");
		layer = tag.contains("Layer") ? tag.getInt("Layer") : Integer.MIN_VALUE;
		finished = tag.getBoolean("Finished");
	}

	@Override
	protected void saveExtra(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putInt("Cursor", cursor);
		tag.putInt("Layer", layer);
		tag.putBoolean("Finished", finished);
	}
}
