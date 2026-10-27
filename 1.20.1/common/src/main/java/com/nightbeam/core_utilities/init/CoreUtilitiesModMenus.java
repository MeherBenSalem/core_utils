package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.machine.MachineMenu;
import com.nightbeam.core_utilities.machine.MachineSpec;
import com.nightbeam.core_utilities.machine.MachineSpecs;
import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class CoreUtilitiesModMenus {
	public static Supplier<MenuType<?>> SMALL_BACKPACK_GUI;
	public static Supplier<MenuType<?>> MEDIUM_BACKPACK_GUI;
	public static Supplier<MenuType<?>> BIG_BACKPACK_GUI;

	/** Every machine shares {@link MachineMenu}; each gets its own menu type so the client knows the layout. */
	public static void registerMachines() {
		for (MachineSpec spec : MachineSpecs.ALL) {
			Supplier<MenuType<MachineMenu>> type = Services.PLATFORM.register(BuiltInRegistries.MENU, spec.name(), () -> Services.PLATFORM.createMenuType((id, inventory) -> new MachineMenu(spec, id, inventory)));
			spec.setMenuType(type);
		}
	}
}
