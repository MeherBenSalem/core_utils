package com.nightbeam.core_utilities.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Places blocks from its inventory in front of it. */
public class BlockPlacerBlockEntity extends AreaMachineBlockEntity {
	public BlockPlacerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec) {
		super(type, pos, state, spec, 20_000);
	}

	@Override
	protected void defaultSides(SideMode[] sides) {
		java.util.Arrays.fill(sides, SideMode.INPUT);
		sides[RelativeSide.FRONT.ordinal()] = SideMode.NONE;
	}

	@Override
	public boolean isValidInput(int slot, ItemStack stack) {
		return stack.getItem() instanceof BlockItem;
	}

	@Override
	public boolean isUpgradeSupported(com.nightbeam.core_utilities.item.UpgradeItem.Type type) {
		return type != com.nightbeam.core_utilities.item.UpgradeItem.Type.RANGE;
	}

	@Override
	protected boolean work() {
		maxProgress = scaledTime(10);
		int usage = scaledUsage(20);
		Direction facing = getFacing();
		BlockPos target = worldPosition.relative(facing);
		if (!level.getBlockState(target).canBeReplaced() || energy.getEnergyStored() < usage) {
			progress = 0;
			return false;
		}
		for (int slot : spec.indices(SlotKind.INPUT)) {
			ItemStack stack = items.get(slot);
			if (!(stack.getItem() instanceof BlockItem blockItem)) {
				continue;
			}
			progress++;
			if (progress < maxProgress) {
				return true;
			}
			progress = 0;
			InteractionResult result = blockItem.place(new DirectionalPlaceContext(level, target, facing, stack, facing.getOpposite()));
			if (result.consumesAction()) {
				energy.consume(usage);
				lastUsage = usage;
				setChanged();
			}
			return true;
		}
		progress = 0;
		return false;
	}
}
