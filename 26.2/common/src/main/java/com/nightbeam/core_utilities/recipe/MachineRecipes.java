package com.nightbeam.core_utilities.recipe;

import com.nightbeam.core_utilities.init.CoreUtilitiesModItems;
import com.nightbeam.core_utilities.util.Compat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Built-in processing recipes. Inputs use item tags where possible (the {@code core_utilities:} tags include the
 * common {@code c:}/{@code forge:} tags), so ores and ingots from other mods work too.
 */
public final class MachineRecipes {
	private static final Map<RecipeKind, List<MachineRecipe>> RECIPES = new EnumMap<>(RecipeKind.class);
	private static boolean initialized;

	private MachineRecipes() {
	}

	public static List<MachineRecipe> get(RecipeKind kind) {
		init();
		return RECIPES.getOrDefault(kind, List.of());
	}

	public static MachineRecipe find(RecipeKind kind, List<ItemStack> inputs) {
		for (MachineRecipe recipe : get(kind)) {
			if (recipe.match(inputs) != null) {
				return recipe;
			}
		}
		return null;
	}

	public static boolean isIngredient(RecipeKind kind, ItemStack stack) {
		for (MachineRecipe recipe : get(kind)) {
			if (recipe.accepts(stack)) {
				return true;
			}
		}
		return false;
	}

	private static synchronized void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		// Crusher: ore doubling and handy vanilla conversions
		crush("iron_ore", tag("core_utilities:ores/iron"), 1, item(CoreUtilitiesModItems.IRON_DUST), 2, null, 0, 160);
		crush("gold_ore", tag("core_utilities:ores/gold"), 1, item(CoreUtilitiesModItems.GOLD_DUST), 2, null, 0, 160);
		crush("copper_ore", tag("core_utilities:ores/copper"), 1, item(CoreUtilitiesModItems.COPPER_DUST), 6, null, 0, 160);
		crush("raw_iron", tag("core_utilities:raw_materials/iron"), 1, item(CoreUtilitiesModItems.IRON_DUST), 1, item(CoreUtilitiesModItems.IRON_DUST), 0.5f, 120);
		crush("raw_gold", tag("core_utilities:raw_materials/gold"), 1, item(CoreUtilitiesModItems.GOLD_DUST), 1, item(CoreUtilitiesModItems.GOLD_DUST), 0.5f, 120);
		crush("raw_copper", tag("core_utilities:raw_materials/copper"), 1, item(CoreUtilitiesModItems.COPPER_DUST), 1, item(CoreUtilitiesModItems.COPPER_DUST), 0.5f, 120);
		crush("iron_ingot", tag("core_utilities:ingots/iron"), 1, item(CoreUtilitiesModItems.IRON_DUST), 1, null, 0, 100);
		crush("gold_ingot", tag("core_utilities:ingots/gold"), 1, item(CoreUtilitiesModItems.GOLD_DUST), 1, null, 0, 100);
		crush("copper_ingot", tag("core_utilities:ingots/copper"), 1, item(CoreUtilitiesModItems.COPPER_DUST), 1, null, 0, 100);
		crush("coal", vanilla(Items.COAL), 1, item(CoreUtilitiesModItems.COAL_DUST), 1, null, 0, 80);
		crush("charcoal", vanilla(Items.CHARCOAL), 1, item(CoreUtilitiesModItems.COAL_DUST), 1, null, 0, 80);
		crush("coal_ore", tag("core_utilities:ores/coal"), 1, vanillaOut(Items.COAL), 3, item(CoreUtilitiesModItems.COAL_DUST), 0.5f, 160);
		crush("diamond_ore", tag("core_utilities:ores/diamond"), 1, vanillaOut(Items.DIAMOND), 2, null, 0, 200);
		crush("emerald_ore", tag("core_utilities:ores/emerald"), 1, vanillaOut(Items.EMERALD), 2, null, 0, 200);
		crush("lapis_ore", tag("core_utilities:ores/lapis"), 1, vanillaOut(Items.LAPIS_LAZULI), 12, null, 0, 160);
		crush("redstone_ore", tag("core_utilities:ores/redstone"), 1, vanillaOut(Items.REDSTONE), 8, null, 0, 160);
		crush("quartz_ore", tag("core_utilities:ores/quartz"), 1, vanillaOut(Items.QUARTZ), 3, null, 0, 160);
		crush("stone", vanilla(Items.STONE), 1, vanillaOut(Items.COBBLESTONE), 1, null, 0, 60);
		crush("cobblestone", vanilla(Items.COBBLESTONE), 1, vanillaOut(Items.GRAVEL), 1, null, 0, 80);
		crush("gravel", vanilla(Items.GRAVEL), 1, vanillaOut(Items.SAND), 1, vanillaOut(Items.FLINT), 0.1f, 80);
		crush("blaze_rod", vanilla(Items.BLAZE_ROD), 1, vanillaOut(Items.BLAZE_POWDER), 4, null, 0, 80);
		crush("bone", vanilla(Items.BONE), 1, vanillaOut(Items.BONE_MEAL), 6, null, 0, 60);
		crush("wool", tag("minecraft:wool"), 1, vanillaOut(Items.STRING), 4, null, 0, 60);
		crush("glowstone", vanilla(Items.GLOWSTONE), 1, vanillaOut(Items.GLOWSTONE_DUST), 4, null, 0, 60);
		crush("sugar_cane", vanilla(Items.SUGAR_CANE), 1, vanillaOut(Items.SUGAR), 2, null, 0, 60);
		crush("clay", vanilla(Items.CLAY), 1, vanillaOut(Items.CLAY_BALL), 4, null, 0, 60);
		// Alloy smelter: the progression metals
		alloy("steel_ingot", tag("core_utilities:ingots/iron"), 1, ingredient(CoreUtilitiesModItems.COAL_DUST), 2, item(CoreUtilitiesModItems.STEEL_INGOT), 1, 200);
		alloy("energized_ingot", tag("core_utilities:ingots/gold"), 1, vanilla(Items.REDSTONE), 4, item(CoreUtilitiesModItems.ENERGIZED_INGOT), 1, 200);
		alloy("ender_alloy_ingot", ingredient(CoreUtilitiesModItems.ENERGIZED_INGOT), 1, vanilla(Items.ENDER_PEARL), 1, item(CoreUtilitiesModItems.ENDER_ALLOY_INGOT), 1, 300);
		alloy("netherite_ingot", vanilla(Items.NETHERITE_SCRAP), 4, tag("core_utilities:ingots/gold"), 4, vanillaOut(Items.NETHERITE_INGOT), 1, 400);
		// Compressor: plates and dense blocks
		compress("iron_plate", tag("core_utilities:ingots/iron"), 1, item(CoreUtilitiesModItems.IRON_PLATE), 1, 100);
		compress("copper_plate", tag("core_utilities:ingots/copper"), 1, item(CoreUtilitiesModItems.COPPER_PLATE), 1, 100);
		compress("gold_plate", tag("core_utilities:ingots/gold"), 1, item(CoreUtilitiesModItems.GOLD_PLATE), 1, 100);
		compress("steel_plate", ingredient(CoreUtilitiesModItems.STEEL_INGOT), 1, item(CoreUtilitiesModItems.STEEL_PLATE), 1, 140);
		compress("diamond", ingredient(CoreUtilitiesModItems.COAL_DUST), 64, vanillaOut(Items.DIAMOND), 1, 600);
		compress("snow_block", vanilla(Items.SNOWBALL), 4, vanillaOut(Items.SNOW_BLOCK), 1, 40);
		compress("packed_ice", vanilla(Items.ICE), 9, vanillaOut(Items.PACKED_ICE), 1, 80);
		compress("blue_ice", vanilla(Items.PACKED_ICE), 9, vanillaOut(Items.BLUE_ICE), 1, 80);
		compress("sandstone", tag("minecraft:sand"), 4, vanillaOut(Items.SANDSTONE), 1, 60);
	}

	private static Supplier<Ingredient> tag(String id) {
		return () -> Compat.tag(id);
	}

	private static Supplier<Ingredient> vanilla(ItemLike item) {
		return () -> Compat.item(item);
	}

	private static Supplier<Ingredient> ingredient(Supplier<? extends ItemLike> item) {
		return () -> Compat.item(item.get());
	}

	private static Supplier<? extends ItemLike> item(Supplier<? extends ItemLike> item) {
		return item;
	}

	private static Supplier<? extends ItemLike> vanillaOut(ItemLike item) {
		return () -> item;
	}

	private static void crush(String id, Supplier<Ingredient> input, int count, Supplier<? extends ItemLike> output, int outCount, Supplier<? extends ItemLike> secondary, float chance, int ticks) {
		add(RecipeKind.CRUSHING, new MachineRecipe("crushing/" + id, List.of(new MachineRecipe.CountedIngredient(input, count)), stack(output, outCount), secondary == null ? null : stack(secondary, 1), chance, ticks));
	}

	private static void alloy(String id, Supplier<Ingredient> a, int countA, Supplier<Ingredient> b, int countB, Supplier<? extends ItemLike> output, int outCount, int ticks) {
		add(RecipeKind.ALLOYING, new MachineRecipe("alloying/" + id, List.of(new MachineRecipe.CountedIngredient(a, countA), new MachineRecipe.CountedIngredient(b, countB)), stack(output, outCount), null, 0, ticks));
	}

	private static void compress(String id, Supplier<Ingredient> input, int count, Supplier<? extends ItemLike> output, int outCount, int ticks) {
		add(RecipeKind.COMPRESSING, new MachineRecipe("compressing/" + id, List.of(new MachineRecipe.CountedIngredient(input, count)), stack(output, outCount), null, 0, ticks));
	}

	private static Supplier<ItemStack> stack(Supplier<? extends ItemLike> item, int count) {
		return () -> new ItemStack(item.get(), count);
	}

	private static void add(RecipeKind kind, MachineRecipe recipe) {
		RECIPES.computeIfAbsent(kind, k -> new ArrayList<>()).add(recipe);
	}
}
