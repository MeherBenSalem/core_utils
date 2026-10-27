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
						.icon(() -> new ItemStack(CoreUtilitiesModItems.QUARRY.get()))
						.displayItems((parameters, tabData) -> CoreUtilitiesModTabContents.fill(tabData))
						.build());
	}
}
