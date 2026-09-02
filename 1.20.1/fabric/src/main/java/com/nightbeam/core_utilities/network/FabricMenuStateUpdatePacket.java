package com.nightbeam.core_utilities.network;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.init.MenuAccessor;
import com.nightbeam.core_utilities.init.ScreenAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class FabricMenuStateUpdatePacket {
	public static final ResourceLocation ID = new ResourceLocation(CoreUtilitiesMod.MOD_ID, "menu_state_update");

	private FabricMenuStateUpdatePacket() {
	}

	public static FriendlyByteBuf create(int elementType, String name, Object elementState) {
		FriendlyByteBuf buffer = PacketByteBufs.create();
		buffer.writeInt(elementType);
		buffer.writeUtf(name);
		if (elementType == 0) {
			buffer.writeUtf((String) elementState);
		} else if (elementType == 1) {
			buffer.writeBoolean((boolean) elementState);
		}
		return buffer;
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
			final int elementType = buf.readInt();
			final String name = buf.readUtf();
			final Object elementState;
			if (elementType == 0) {
				elementState = buf.readUtf();
			} else if (elementType == 1) {
				elementState = buf.readBoolean();
			} else {
				elementState = null;
			}
			if (name.length() > 256 || elementState instanceof String string && string.length() > 8192) {
				return;
			}
			server.execute(() -> {
				if (player.containerMenu instanceof MenuAccessor menu) {
					menu.getMenuState().put(elementType + ":" + name, elementState);
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
			final int elementType = buf.readInt();
			final String name = buf.readUtf();
			final Object elementState;
			if (elementType == 0) {
				elementState = buf.readUtf();
			} else if (elementType == 1) {
				elementState = buf.readBoolean();
			} else {
				elementState = null;
			}
			client.execute(() -> {
				if (client.player != null && client.player.containerMenu instanceof MenuAccessor menu) {
					menu.getMenuState().put(elementType + ":" + name, elementState);
				}
				if (client.screen instanceof ScreenAccessor accessor) {
					accessor.updateMenuState(elementType, name, elementState);
				}
			});
		});
	}
}
