package com.nightbeam.core_utilities.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Supplier;

/**
 * A processing recipe for the Crusher, Alloy Smelter or Compressor.
 *
 * @param inputs          ingredients with counts, matched in any order against the input slots
 * @param output          main product
 * @param secondary       optional by-product
 * @param secondaryChance chance (0..1) for the by-product
 * @param ticks           base processing time
 */
public record MachineRecipe(String id, List<CountedIngredient> inputs, Supplier<ItemStack> output, Supplier<ItemStack> secondary, float secondaryChance, int ticks) {
	public record CountedIngredient(Supplier<Ingredient> ingredient, int count) {
		public boolean test(ItemStack stack) {
			return !stack.isEmpty() && stack.getCount() >= count && ingredient.get().test(stack);
		}

		public boolean matchesItem(ItemStack stack) {
			return !stack.isEmpty() && ingredient.get().test(stack);
		}
	}

	public ItemStack result() {
		return output.get().copy();
	}

	public ItemStack byproduct() {
		return secondary == null ? ItemStack.EMPTY : secondary.get().copy();
	}

	/** Returns, for each ingredient, the input slot index it was matched to, or null if the inputs don't match. */
	public int[] match(List<ItemStack> slots) {
		int[] used = new int[inputs.size()];
		boolean[] taken = new boolean[slots.size()];
		for (int i = 0; i < inputs.size(); i++) {
			used[i] = -1;
			for (int s = 0; s < slots.size(); s++) {
				if (!taken[s] && inputs.get(i).test(slots.get(s))) {
					used[i] = s;
					taken[s] = true;
					break;
				}
			}
			if (used[i] < 0) {
				return null;
			}
		}
		return used;
	}

	/** Whether the stack could be part of this recipe (used to validate slot inserts). */
	public boolean accepts(ItemStack stack) {
		for (CountedIngredient input : inputs) {
			if (input.matchesItem(stack)) {
				return true;
			}
		}
		return false;
	}
}
