package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.item.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Shared helpers for machines that work on blocks or entities around them. */
public abstract class AreaMachineBlockEntity extends MachineBlockEntity {
	public static final TagKey<Block> DENY = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, com.nightbeam.core_utilities.util.Compat.id("drill_machine"));
	private static final ItemStack TOOL = new ItemStack(Items.NETHERITE_PICKAXE);

	protected AreaMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec, int capacity) {
		super(type, pos, state, spec, capacity, Math.max(1000, capacity / 20), 0);
	}

	@Override
	protected void defaultSides(SideMode[] sides) {
		java.util.Arrays.fill(sides, SideMode.OUTPUT);
	}

	@Override
	public boolean isUpgradeSupported(UpgradeItem.Type type) {
		return true;
	}

	protected int radius(int base, int max) {
		return Math.min(max, base + 2 * countUpgrades(UpgradeItem.Type.RANGE));
	}

	/** Whether a machine may break this block: not air, not a fluid, not unbreakable, not a block entity, not deny-listed. */
	protected boolean canBreak(BlockPos pos, BlockState state) {
		if (state.isAir() || state.liquid() || !state.getFluidState().isEmpty()) {
			return false;
		}
		return state.getDestroySpeed(level, pos) >= 0 && !state.is(DENY) && level.getBlockEntity(pos) == null;
	}

	/** Breaks the block and returns its drops (as if mined with a netherite pickaxe), or null if the output is full. */
	protected List<ItemStack> breakBlock(BlockPos pos, BlockState state) {
		BlockEntity entity = level.getBlockEntity(pos);
		List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, entity, null, TOOL);
		level.levelEvent(2001, pos, Block.getId(state));
		level.removeBlock(pos, false);
		return drops;
	}

	/** Stores drops in the output slots; returns false if something did not fit (it is dropped in world). */
	protected boolean store(List<ItemStack> drops) {
		int[] outputs = spec.indices(SlotKind.OUTPUT);
		boolean allStored = true;
		for (ItemStack drop : drops) {
			ItemStack rest = output(drop, outputs);
			if (!rest.isEmpty()) {
				allStored = false;
				net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5, rest);
			}
		}
		setChanged();
		return allStored;
	}

	protected boolean hasOutputSpace() {
		for (int slot : spec.indices(SlotKind.OUTPUT)) {
			if (items.get(slot).isEmpty()) {
				return true;
			}
		}
		return false;
	}
}
