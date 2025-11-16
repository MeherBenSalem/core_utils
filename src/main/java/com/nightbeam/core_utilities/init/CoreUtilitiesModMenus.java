/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.nightbeam.core_utilities.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.Minecraft;

import java.util.Map;

import com.nightbeam.core_utilities.world.inventory.SmallBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.MediumBackpackGUIMenu;
import com.nightbeam.core_utilities.world.inventory.BigBackpackGUIMenu;
import com.nightbeam.core_utilities.network.MenuStateUpdateMessage;
import com.nightbeam.core_utilities.CoreUtilitiesMod;

public class CoreUtilitiesModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CoreUtilitiesMod.MODID);
	public static final RegistryObject<MenuType<SmallBackpackGUIMenu>> SMALL_BACKPACK_GUI = REGISTRY.register("small_backpack_gui", () -> IForgeMenuType.create(SmallBackpackGUIMenu::new));
	public static final RegistryObject<MenuType<BigBackpackGUIMenu>> BIG_BACKPACK_GUI = REGISTRY.register("big_backpack_gui", () -> IForgeMenuType.create(BigBackpackGUIMenu::new));
	public static final RegistryObject<MenuType<MediumBackpackGUIMenu>> MEDIUM_BACKPACK_GUI = REGISTRY.register("medium_backpack_gui", () -> IForgeMenuType.create(MediumBackpackGUIMenu::new));

	public interface MenuAccessor {
		Map<String, Object> getMenuState();

		Map<Integer, Slot> getSlots();

		default void sendMenuStateUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
			getMenuState().put(elementType + ":" + name, elementState);
			if (player instanceof ServerPlayer serverPlayer) {
				CoreUtilitiesMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new MenuStateUpdateMessage(elementType, name, elementState));
			} else if (player.level().isClientSide) {
				if (Minecraft.getInstance().screen instanceof CoreUtilitiesModScreens.ScreenAccessor accessor && needClientUpdate)
					accessor.updateMenuState(elementType, name, elementState);
				CoreUtilitiesMod.PACKET_HANDLER.sendToServer(new MenuStateUpdateMessage(elementType, name, elementState));
			}
		}

		default <T> T getMenuState(int elementType, String name, T defaultValue) {
			try {
				return (T) getMenuState().getOrDefault(elementType + ":" + name, defaultValue);
			} catch (ClassCastException e) {
				return defaultValue;
			}
		}
	}
}