package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CoreUtilitiesModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoreUtilitiesMod.MOD_ID);
	public static final RegistryObject<CreativeModeTab> ESSENTIAL_UTILITIES_CREATIVE_TAB = REGISTRY.register("essential_utilities_creative_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("item_group.core_utilities.essential_utilities_creative_tab"))
					.icon(() -> new ItemStack(CoreUtilitiesModItems.QUARRY.get()))
					.displayItems((parameters, tabData) -> CoreUtilitiesModTabContents.fill(tabData)).build());
}
