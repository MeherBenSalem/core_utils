package com.nightbeam.core_utilities.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Supplier;

/** Creative tab order, shared by every loader. */
public final class CoreUtilitiesModTabContents {
	private CoreUtilitiesModTabContents() {
	}

	public static void fill(CreativeModeTab.Output output) {
		List<Supplier<Item>> items = List.of(
				CoreUtilitiesModItems.GUIDE_BOOK, CoreUtilitiesModItems.CONFIGURATION_WRENCH, CoreUtilitiesModItems.MULTIMETER,
				CoreUtilitiesModItems.SOLAR_PANEL, CoreUtilitiesModItems.SOLAR_PANEL_TIER_21, CoreUtilitiesModItems.SOLAR_PANEL_TIER_31, CoreUtilitiesModItems.SOLAR_PANEL_TIER_41,
				CoreUtilitiesModItems.COMBUSTION_GENERATOR, CoreUtilitiesModItems.GEOTHERMAL_GENERATOR,
				CoreUtilitiesModItems.ENERGY_CELL_BASIC, CoreUtilitiesModItems.ENERGY_CELL_ADVANCED, CoreUtilitiesModItems.ENERGY_CELL_ELITE, CoreUtilitiesModItems.ENERGY_CELL_CREATIVE,
				CoreUtilitiesModItems.CABLE, CoreUtilitiesModItems.CABLE_ADVANCED, CoreUtilitiesModItems.CABLE_ELITE,
				CoreUtilitiesModItems.ITEM_PIPE, CoreUtilitiesModItems.RAPID_ITEM_PIPE,
				CoreUtilitiesModItems.ELECTRIC_FURNACE, CoreUtilitiesModItems.CRUSHER, CoreUtilitiesModItems.ALLOY_SMELTER, CoreUtilitiesModItems.COMPRESSOR,
				CoreUtilitiesModItems.QUARRY, CoreUtilitiesModItems.CROP_HARVESTER, CoreUtilitiesModItems.MOB_GRINDER, CoreUtilitiesModItems.BLOCK_BREAKER, CoreUtilitiesModItems.BLOCK_PLACER,
				CoreUtilitiesModItems.BASIC_MACHINE_FRAME, CoreUtilitiesModItems.ADVANCED_MACHINE_FRAME, CoreUtilitiesModItems.STEEL_BLOCK,
				CoreUtilitiesModItems.SPEED_UPGRADE, CoreUtilitiesModItems.ENERGY_UPGRADE, CoreUtilitiesModItems.RANGE_UPGRADE,
				CoreUtilitiesModItems.BASIC_CIRCUIT, CoreUtilitiesModItems.ADVANCED_CIRCUIT, CoreUtilitiesModItems.ELITE_CIRCUIT,
				CoreUtilitiesModItems.IRON_DUST, CoreUtilitiesModItems.GOLD_DUST, CoreUtilitiesModItems.COPPER_DUST, CoreUtilitiesModItems.COAL_DUST,
				CoreUtilitiesModItems.STEEL_INGOT, CoreUtilitiesModItems.ENERGIZED_INGOT, CoreUtilitiesModItems.ENDER_ALLOY_INGOT,
				CoreUtilitiesModItems.IRON_PLATE, CoreUtilitiesModItems.COPPER_PLATE, CoreUtilitiesModItems.GOLD_PLATE, CoreUtilitiesModItems.STEEL_PLATE,
				CoreUtilitiesModItems.BLANK_PLATE,
				CoreUtilitiesModItems.VECTOR_PLATE_TIER_1, CoreUtilitiesModItems.VECTOR_PLATE_TIER_2, CoreUtilitiesModItems.VECTOR_PLATE_TIER_3, CoreUtilitiesModItems.VECTOR_PLATE_TIER_4,
				CoreUtilitiesModItems.HEALING_PLATE, CoreUtilitiesModItems.SPEED_PLATE, CoreUtilitiesModItems.FIRE_RES_PLATE, CoreUtilitiesModItems.STRENGTH_PLATE,
				CoreUtilitiesModItems.POISON_PLATE, CoreUtilitiesModItems.WEAKNESS_PLATE, CoreUtilitiesModItems.SLOWNESS_PLATE, CoreUtilitiesModItems.WITHER_PLATE,
				CoreUtilitiesModItems.PLAYER_FILTER, CoreUtilitiesModItems.MOB_FILTER, CoreUtilitiesModItems.NEUTRAL_FILTER, CoreUtilitiesModItems.PROJECTILE_FILTER,
				CoreUtilitiesModItems.FILTER_ZOMBIE, CoreUtilitiesModItems.FILTER_SKELETON, CoreUtilitiesModItems.FILTER_SPIDER, CoreUtilitiesModItems.FILTER_VILLAGER,
				CoreUtilitiesModItems.FILTER_PET, CoreUtilitiesModItems.FILTER_ANIMAL,
				CoreUtilitiesModItems.SMALL_BACKPACK, CoreUtilitiesModItems.MEDIUM_BACKPACK, CoreUtilitiesModItems.BIG_BACKPACK,
				CoreUtilitiesModItems.MOB_NET, CoreUtilitiesModItems.FLOATING_BLOCK);
		for (Supplier<Item> item : items) {
			if (item != null) {
				output.accept(item.get());
			}
		}
	}
}
