package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.world.inventory.BigBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.MediumBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.SmallBackpackGUIMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CoreUtilitiesModMenusForge {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CoreUtilitiesMod.MOD_ID);
	public static final RegistryObject<MenuType<SmallBackpackGUIMenu>> SMALL_BACKPACK_GUI = REGISTRY.register("small_backpack_gui",
			() -> IForgeMenuType.create(SmallBackpackGUIMenu::new));
	public static final RegistryObject<MenuType<BigBackpackGUIMenu>> BIG_BACKPACK_GUI = REGISTRY.register("big_backpack_gui",
			() -> IForgeMenuType.create(BigBackpackGUIMenu::new));
	public static final RegistryObject<MenuType<MediumBackpackGUIMenu>> MEDIUM_BACKPACK_GUI = REGISTRY.register("medium_backpack_gui",
			() -> IForgeMenuType.create(MediumBackpackGUIMenu::new));

	static {
		CoreUtilitiesModMenus.SMALL_BACKPACK_GUI = () -> SMALL_BACKPACK_GUI.get();
		CoreUtilitiesModMenus.BIG_BACKPACK_GUI = () -> BIG_BACKPACK_GUI.get();
		CoreUtilitiesModMenus.MEDIUM_BACKPACK_GUI = () -> MEDIUM_BACKPACK_GUI.get();
	}
}
