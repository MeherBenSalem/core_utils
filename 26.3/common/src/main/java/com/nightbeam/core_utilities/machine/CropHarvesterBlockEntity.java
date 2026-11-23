package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Harvests mature crops around itself (same layer and one above), replants them from the drops and can apply
 * bone meal from its fertilizer slot to speed things up.
 */
public class CropHarvesterBlockEntity extends AreaMachineBlockEntity {
	private int cursor;
	private int cooldown;

	public CropHarvesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec) {
		super(type, pos, state, spec, 40_000);
	}

	@Override
	protected void defaultSides(SideMode[] sides) {
		java.util.Arrays.fill(sides, SideMode.BOTH);
	}

	@Override
	public boolean isValidInput(int slot, ItemStack stack) {
		return stack.is(Items.BONE_MEAL);
	}

	@Override
	protected boolean work() {
		int radius = radius(4, 12);
		int side = radius * 2 + 1;
		int usage = scaledUsage(CoreUtilitiesConfig.get().harvesterEnergyPerAction);
		maxProgress = side * side * 2;
		progress = cursor;
		if (!hasOutputSpace() || energy.getEnergyStored() < usage) {
			return false;
		}
		if (cooldown > 0) {
			cooldown--;
			return true;
		}
		for (int scanned = 0; scanned < side * side * 2; scanned++) {
			cursor = (cursor + 1) % (side * side * 2);
			int index = cursor % (side * side);
			BlockPos target = new BlockPos(worldPosition.getX() - radius + index % side, worldPosition.getY() + cursor / (side * side), worldPosition.getZ() - radius + index / side);
			if (target.equals(worldPosition) || !level.isLoaded(target)) {
				continue;
			}
			BlockState state = level.getBlockState(target);
			if (harvest(target, state) || fertilize(target, state)) {
				energy.consume(usage);
				lastUsage = usage;
				cooldown = scaledTime(10) - 1;
				setChanged();
				return true;
			}
		}
		return true;
	}

	private boolean fertilize(BlockPos pos, BlockState state) {
		int slot = spec.indices(SlotKind.INPUT)[0];
		ItemStack meal = items.get(slot);
		if (meal.isEmpty() || !(state.getBlock() instanceof CropBlock crop) || crop.isMaxAge(state)) {
			return false;
		}
		if (state.getBlock() instanceof BonemealableBlock && BoneMealItem.growCrop(meal, level, pos)) {
			level.levelEvent(1505, pos, 15);
			return true;
		}
		return false;
	}

	private boolean harvest(BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if (block instanceof CropBlock crop) {
			if (!crop.isMaxAge(state)) {
				return false;
			}
			List<ItemStack> drops = Block.getDrops(state, (net.minecraft.server.level.ServerLevel) level, pos, null);
			ItemStack seed = state.getCloneItemStack(level, pos, false);
			consumeOne(drops, seed);
			level.setBlock(pos, crop.getStateForAge(0), Block.UPDATE_ALL);
			level.levelEvent(2001, pos, Block.getId(state));
			store(drops);
			return true;
		}
		if (block instanceof NetherWartBlock) {
			if (state.getValue(NetherWartBlock.AGE) < 3) {
				return false;
			}
			List<ItemStack> drops = Block.getDrops(state, (net.minecraft.server.level.ServerLevel) level, pos, null);
			consumeOne(drops, new ItemStack(Items.NETHER_WART));
			level.setBlock(pos, state.setValue(NetherWartBlock.AGE, 0), Block.UPDATE_ALL);
			store(drops);
			return true;
		}
		if (block instanceof CocoaBlock) {
			if (state.getValue(CocoaBlock.AGE) < 2) {
				return false;
			}
			List<ItemStack> drops = Block.getDrops(state, (net.minecraft.server.level.ServerLevel) level, pos, null);
			consumeOne(drops, new ItemStack(Items.COCOA_BEANS));
			level.setBlock(pos, state.setValue(CocoaBlock.AGE, 0), Block.UPDATE_ALL);
			store(drops);
			return true;
		}
		if (block instanceof SweetBerryBushBlock) {
			if (state.getValue(SweetBerryBushBlock.AGE) < 3) {
				return false;
			}
			level.setBlock(pos, state.setValue(SweetBerryBushBlock.AGE, 1), Block.UPDATE_ALL);
			store(List.of(new ItemStack(Items.SWEET_BERRIES, 2 + level.getRandom().nextInt(2))));
			return true;
		}
		if (state.is(Blocks.MELON) || state.is(Blocks.PUMPKIN)) {
			store(breakBlock(pos, state));
			return true;
		}
		if ((block instanceof SugarCaneBlock || block instanceof CactusBlock) && level.getBlockState(pos.below()).is(block)) {
			if (level.getBlockState(pos.above()).is(block)) {
				return false;
			}
			store(breakBlock(pos, state));
			return true;
		}
		return false;
	}

	private static void consumeOne(List<ItemStack> drops, ItemStack seed) {
		for (ItemStack drop : drops) {
			if (ItemStack.isSameItem(drop, seed)) {
				drop.shrink(1);
				return;
			}
		}
	}
}
