package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.world.inventory.BigBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.MediumBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.SmallBackpackGUIMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CoreUtilitiesModMenusForge {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, CoreUtilitiesMod.MOD_ID);
	public static final DeferredHolder<MenuType<?>, MenuType<SmallBackpackGUIMenu>> SMALL_BACKPACK_GUI = REGISTRY.register("small_backpack_gui",
			() -> IMenuTypeExtension.create(SmallBackpackGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BigBackpackGUIMenu>> BIG_BACKPACK_GUI = REGISTRY.register("big_backpack_gui",
			() -> IMenuTypeExtension.create(BigBackpackGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<MediumBackpackGUIMenu>> MEDIUM_BACKPACK_GUI = REGISTRY.register("medium_backpack_gui",
			() -> IMenuTypeExtension.create(MediumBackpackGUIMenu::new));

	static {
		CoreUtilitiesModMenus.SMALL_BACKPACK_GUI = () -> SMALL_BACKPACK_GUI.get();
		CoreUtilitiesModMenus.BIG_BACKPACK_GUI = () -> BIG_BACKPACK_GUI.get();
		CoreUtilitiesModMenus.MEDIUM_BACKPACK_GUI = () -> MEDIUM_BACKPACK_GUI.get();
	}
}
