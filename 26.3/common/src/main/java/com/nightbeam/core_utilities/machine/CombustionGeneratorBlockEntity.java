package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import com.nightbeam.core_utilities.item.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
			int burn = burnTime(fuel);
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

	/**
	 * A fuel's furnace burn time in ticks. 26.3 no longer keeps fuel values per level: the burn time is the item's
	 * cooking_fuel component, resolved in a container-process loot context (modded fuels set the same component).
	 */
	private int burnTime(ItemStack fuel) {
		if (fuel.isEmpty() || !(level instanceof ServerLevel server)) {
			return 0;
		}
		LootContext context = new LootContext.Builder(new LootParams.Builder(server)
				.withParameter(LootContextParams.BLOCK_STATE, getBlockState())
				.withParameter(LootContextParams.BLOCK_ENTITY, this)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(getBlockPos()))
				.withParameter(LootContextParams.CONTAINER, this)
				.create(LootContextParamSets.CONTAINER_PROCESS))
				.create(Optional.empty());
		return ResolvableInt.getFromItem(fuel, DataComponents.COOKING_FUEL, CookingFuel::burnTime, context, 0);
	}

	@Override
	public boolean isValidInput(int slot, ItemStack stack) {
		// Works on both sides (slot checks also run on the client): any item with a cooking_fuel component is fuel.
		return stack.has(DataComponents.COOKING_FUEL);
	}

	@Override
	protected boolean canReceiveEnergyFrom(@Nullable Direction side) {
		return false;
	}
}
