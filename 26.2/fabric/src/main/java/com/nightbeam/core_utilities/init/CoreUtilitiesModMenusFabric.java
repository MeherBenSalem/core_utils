package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.world.inventory.BigBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.MediumBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.SmallBackpackGUIMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

public class CoreUtilitiesModMenusFabric {
	private static MenuType<SmallBackpackGUIMenu> smallMenu;
	private static MenuType<MediumBackpackGUIMenu> mediumMenu;
	private static MenuType<BigBackpackGUIMenu> bigMenu;

	public static MenuType<SmallBackpackGUIMenu> getSmallMenu() { return smallMenu; }
	public static MenuType<MediumBackpackGUIMenu> getMediumMenu() { return mediumMenu; }
	public static MenuType<BigBackpackGUIMenu> getBigMenu() { return bigMenu; }

	public static void register() {
		smallMenu = Registry.register(BuiltInRegistries.MENU, id("small_backpack_gui"),
				new ExtendedMenuType<>((syncId, inventory, data) -> new SmallBackpackGUIMenu(syncId, inventory, data), BlockPos.STREAM_CODEC));
		mediumMenu = Registry.register(BuiltInRegistries.MENU, id("medium_backpack_gui"),
				new ExtendedMenuType<>((syncId, inventory, data) -> new MediumBackpackGUIMenu(syncId, inventory, data), BlockPos.STREAM_CODEC));
		bigMenu = Registry.register(BuiltInRegistries.MENU, id("big_backpack_gui"),
				new ExtendedMenuType<>((syncId, inventory, data) -> new BigBackpackGUIMenu(syncId, inventory, data), BlockPos.STREAM_CODEC));
		CoreUtilitiesModMenus.SMALL_BACKPACK_GUI = () -> smallMenu;
		CoreUtilitiesModMenus.MEDIUM_BACKPACK_GUI = () -> mediumMenu;
		CoreUtilitiesModMenus.BIG_BACKPACK_GUI = () -> bigMenu;
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, path);
	}
}