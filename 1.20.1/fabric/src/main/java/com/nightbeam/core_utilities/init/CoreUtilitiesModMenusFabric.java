package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.world.inventory.BigBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.MediumBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.SmallBackpackGUIMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class CoreUtilitiesModMenusFabric {
	private static MenuType<SmallBackpackGUIMenu> smallMenu;
	private static MenuType<MediumBackpackGUIMenu> mediumMenu;
	private static MenuType<BigBackpackGUIMenu> bigMenu;

	public static MenuType<SmallBackpackGUIMenu> getSmallMenu() {
		return smallMenu;
	}

	public static MenuType<MediumBackpackGUIMenu> getMediumMenu() {
		return mediumMenu;
	}

	public static MenuType<BigBackpackGUIMenu> getBigMenu() {
		return bigMenu;
	}

	public static void register() {
		smallMenu = Registry.register(BuiltInRegistries.MENU, id("small_backpack_gui"),
				new ExtendedScreenHandlerType<>(SmallBackpackGUIMenu::new));
		mediumMenu = Registry.register(BuiltInRegistries.MENU, id("medium_backpack_gui"),
				new ExtendedScreenHandlerType<>(MediumBackpackGUIMenu::new));
		bigMenu = Registry.register(BuiltInRegistries.MENU, id("big_backpack_gui"),
				new ExtendedScreenHandlerType<>(BigBackpackGUIMenu::new));
		CoreUtilitiesModMenus.SMALL_BACKPACK_GUI = () -> smallMenu;
		CoreUtilitiesModMenus.MEDIUM_BACKPACK_GUI = () -> mediumMenu;
		CoreUtilitiesModMenus.BIG_BACKPACK_GUI = () -> bigMenu;
	}

	private static ResourceLocation id(String path) {
		return new ResourceLocation(CoreUtilitiesMod.MOD_ID, path);
	}
}
