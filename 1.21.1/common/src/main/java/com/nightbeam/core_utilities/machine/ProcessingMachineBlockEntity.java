package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.recipe.MachineRecipe;
import com.nightbeam.core_utilities.recipe.MachineRecipes;
import com.nightbeam.core_utilities.recipe.RecipeKind;
import com.nightbeam.core_utilities.util.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/** Electric Furnace, Crusher, Alloy Smelter and Compressor. */
public class ProcessingMachineBlockEntity extends MachineBlockEntity {
	private final RecipeKind kind;
	private final int baseUsage;
	private MachineRecipe cached;

	public ProcessingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec, RecipeKind kind, int baseUsage) {
		super(type, pos, state, spec, 20_000, 1_000, 0);
		this.kind = kind;
		this.baseUsage = baseUsage;
	}

	public RecipeKind getKind() {
		return kind;
	}

	private List<ItemStack> inputs() {
		List<ItemStack> list = new ArrayList<>();
		for (int slot : spec.indices(SlotKind.INPUT)) {
			list.add(items.get(slot));
		}
		return list;
	}

	@Override
	protected boolean work() {
		int[] inputSlots = spec.indices(SlotKind.INPUT);
		int[] outputSlots = spec.indices(SlotKind.OUTPUT);
		ItemStack result;
		ItemStack secondary = ItemStack.EMPTY;
		int ticks;
		int[] consumption;
		MachineRecipe recipe = null;
		if (kind == RecipeKind.SMELTING) {
			ItemStack input = items.get(inputSlots[0]);
			if (input.isEmpty()) {
				progress = 0;
				return false;
			}
			result = Compat.smelt(level, input);
			ticks = Math.max(20, Compat.smeltTime(level, input) / 2);
			consumption = null;
		} else {
			List<ItemStack> inputs = inputs();
			if (cached == null || cached.match(inputs) == null) {
				cached = MachineRecipes.find(kind, inputs);
			}
			recipe = cached;
			if (recipe == null) {
				progress = 0;
				return false;
			}
			result = recipe.result();
			secondary = recipe.byproduct();
			ticks = recipe.ticks();
			consumption = recipe.match(inputs);
		}
		if (result.isEmpty() || !canOutput(result, outputSlots) || (!secondary.isEmpty() && outputSlots.length > 1 && !canOutput(secondary, new int[] {outputSlots[1]}))) {
			progress = 0;
			return false;
		}
		maxProgress = scaledTime(ticks);
		int usage = scaledUsage(baseUsage);
		if (!energy.consume(usage)) {
			return false;
		}
		lastUsage = usage;
		progress++;
		if (progress >= maxProgress) {
			progress = 0;
			if (consumption == null) {
				items.get(inputSlots[0]).shrink(1);
				output(result, outputSlots);
			} else {
				for (int i = 0; i < consumption.length; i++) {
					items.get(inputSlots[consumption[i]]).shrink(recipe.inputs().get(i).count());
				}
				output(result, outputSlots.length > 1 ? new int[] {outputSlots[0]} : outputSlots);
				if (!secondary.isEmpty() && level.random.nextFloat() < recipe.secondaryChance()) {
					ItemStack rest = output(secondary, outputSlots.length > 1 ? new int[] {outputSlots[1]} : outputSlots);
					if (!rest.isEmpty()) {
						output(rest, outputSlots);
					}
				}
			}
			setChanged();
		}
		return true;
	}

	@Override
	public boolean isValidInput(int slot, ItemStack stack) {
		if (level == null) {
			return true;
		}
		if (kind == RecipeKind.SMELTING) {
			return !Compat.smelt(level, stack).isEmpty();
		}
		if (!MachineRecipes.isIngredient(kind, stack)) {
			return false;
		}
		// Multi-input machines: don't let automation fill both slots with the same ingredient
		int[] inputSlots = spec.indices(SlotKind.INPUT);
		if (inputSlots.length > 1) {
			for (int other : inputSlots) {
				if (other != slot && ItemStack.isSameItemSameComponents(items.get(other), stack) && !ItemStack.isSameItemSameComponents(items.get(slot), stack)) {
					return false;
				}
			}
		}
		return true;
	}
}
