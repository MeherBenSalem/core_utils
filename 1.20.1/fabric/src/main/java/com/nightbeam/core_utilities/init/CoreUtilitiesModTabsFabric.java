package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CoreUtilitiesModTabsFabric {
	public static CreativeModeTab ESSENTIAL_UTILITIES_CREATIVE_TAB;

	public static void register() {
		ESSENTIAL_UTILITIES_CREATIVE_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
				new ResourceLocation(CoreUtilitiesMod.MOD_ID, "essential_utilities_creative_tab"),
				FabricItemGroup.builder()
						.title(Component.translatable("item_group.core_utilities.essential_utilities_creative_tab"))
						.icon(() -> new ItemStack(CoreUtilitiesModItems.SOLAR_PANEL_TIER_41.get()))
						.displayItems((parameters, tabData) -> {
							tabData.accept(CoreUtilitiesModItems.BLANK_PLATE.get());
							tabData.accept(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_1.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_2.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_3.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_4.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.HEALING_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.SPEED_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.FIRE_RES_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.STRENGTH_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.POISON_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.WEAKNESS_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.SLOWNESS_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.WITHER_PLATE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.PLAYER_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.MOB_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.NEUTRAL_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.PROJECTILE_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.FILTER_ZOMBIE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.FILTER_SKELETON.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.FILTER_SPIDER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.FILTER_VILLAGER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.FILTER_PET.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.FILTER_ANIMAL.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_PLAYER_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_MOB_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_NEUTRAL_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_PROJECTILE_FILTER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_FILTER_ZOMBIE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_FILTER_SKELETON.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_FILTER_SPIDER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_FILTER_VILLAGER.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_FILTER_PET.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.DENY_FILTER_ANIMAL.get().asItem());
							tabData.accept(CoreUtilitiesModItems.CONFIGURATION_WRENCH.get());
							tabData.accept(CoreUtilitiesModItems.SMALL_BACKPACK.get());
							tabData.accept(CoreUtilitiesModItems.MEDIUM_BACKPACK.get());
							tabData.accept(CoreUtilitiesModItems.BIG_BACKPACK.get());
							tabData.accept(CoreUtilitiesModItems.MOB_NET.get());
							tabData.accept(CoreUtilitiesModBlocks.FLOATING_BLOCK.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.SOLAR_PANEL.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.CABLE.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.BASIC_MACHINE_FRAME.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_21.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_31.get().asItem());
							tabData.accept(CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_41.get().asItem());
						}).build());
	}
}
