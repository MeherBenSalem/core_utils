package com.nightbeam.core_utilities.util;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
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

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, path);
	}

	public static Identifier id(String namespace, String path) {
		return Identifier.fromNamespaceAndPath(namespace, path);
	}

	public static TagKey<Item> itemTag(String id) {
		return TagKey.create(Registries.ITEM, Identifier.parse(id));
	}

	public static Ingredient tag(String id) {
		TagKey<Item> tag = itemTag(id);
		return Ingredient.of(BuiltInRegistries.ITEM.get(tag).map(set -> (HolderSet<Item>) set).orElseGet(() -> HolderSet.emptyNamed(BuiltInRegistries.ITEM, tag)));
	}

	public static Ingredient item(ItemLike item) {
		return Ingredient.of(item);
	}

	/** Result of smelting {@code input} in a vanilla furnace, or empty. */
	public static ItemStack smelt(Level level, ItemStack input) {
		Optional<RecipeHolder<SmeltingRecipe>> recipe = smeltingRecipe(level, input);
		return recipe.map(holder -> holder.value().assemble(new SingleRecipeInput(input))).orElse(ItemStack.EMPTY);
	}

	public static int smeltTime(Level level, ItemStack input) {
		return smeltingRecipe(level, input).map(holder -> holder.value().cookingTime()).orElse(200);
	}

	/** 26.x only keeps recipes on the server; clients never see a smelting result. */
	private static Optional<RecipeHolder<SmeltingRecipe>> smeltingRecipe(Level level, ItemStack input) {
		if (!(level instanceof ServerLevel server)) {
			return Optional.empty();
		}
		return server.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level);
	}
}
