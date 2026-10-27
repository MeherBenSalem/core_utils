package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import com.nightbeam.core_utilities.item.UpgradeItem;
import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Burns any furnace fuel (including modded fuels) for FE. Never burns fuel while its buffer is full. */
public class CombustionGeneratorBlockEntity extends MachineBlockEntity {
	public CombustionGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec) {
		super(type, pos, state, spec, 80_000, 0, 4_000);
	}

	@Override
	protected void defaultSides(SideMode[] sides) {
		java.util.Arrays.fill(sides, SideMode.INPUT);
	}

	private int rate() {
		return (int) Math.round(CoreUtilitiesConfig.get().combustionGeneratorRate * (1.0 + 0.5 * countUpgrades(UpgradeItem.Type.SPEED)) * Math.pow(1.1, countUpgrades(UpgradeItem.Type.ENERGY)));
	}

	@Override
	protected boolean work() {
		boolean burning = false;
		int rate = rate();
		if (progress <= 0 && energy.getFreeSpace() >= rate) {
			int slot = spec.indices(SlotKind.FUEL)[0];
			ItemStack fuel = items.get(slot);
			int burn = Services.PLATFORM.getBurnTime(level, fuel);
			if (burn > 0) {
				maxProgress = burn;
				progress = burn;
				ItemStack remainder = fuel.getItem() == Items.LAVA_BUCKET ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY;
				fuel.shrink(1);
				if (fuel.isEmpty() && !remainder.isEmpty()) {
					items.set(slot, remainder);
				}
				setChanged();
			}
		}
		if (progress > 0) {
			int speed = 1 + countUpgrades(UpgradeItem.Type.SPEED) / 2;
			progress = Math.max(0, progress - speed);
			lastUsage = energy.generate(rate);
			burning = true;
			setChanged();
		}
		pushEnergy(rate * 4, d -> true);
		return burning;
	}

	@Override
	public boolean isValidInput(int slot, ItemStack stack) {
		return level == null || Services.PLATFORM.getBurnTime(level, stack) > 0;
	}

	@Override
	protected boolean canReceiveEnergyFrom(@Nullable Direction side) {
		return false;
	}
}
