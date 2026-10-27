package com.nightbeam.core_utilities.util;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Small wrappers around vanilla APIs that changed between Minecraft versions. Each version root has its own
 * copy, so the rest of the common code stays identical.
 */
public final class Compat {
	private Compat() {
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, path);
	}

	public static ResourceLocation id(String namespace, String path) {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	public static TagKey<Item> itemTag(String id) {
		return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
	}

	public static Ingredient tag(String id) {
		return Ingredient.of(itemTag(id));
	}

	public static Ingredient item(ItemLike item) {
		return Ingredient.of(item);
	}

	/** Result of smelting {@code input} in a vanilla furnace, or empty. */
	public static ItemStack smelt(Level level, ItemStack input) {
		Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level);
		return recipe.map(holder -> holder.value().getResultItem(level.registryAccess()).copy()).orElse(ItemStack.EMPTY);
	}

	public static int smeltTime(Level level, ItemStack input) {
		return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level).map(holder -> holder.value().getCookingTime()).orElse(200);
	}
}
