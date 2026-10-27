package com.nightbeam.core_utilities.neoforge;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.init.MenuAccessor;
import com.nightbeam.core_utilities.init.ScreenAccessor;
import com.nightbeam.core_utilities.network.CoreUtilitiesModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NeoForgeNetworking {
	public static final String PROTOCOL_VERSION = "1";
	public static final Identifier MENU_UPDATE_ID = Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, "menu_update");
	public static final Identifier SAVED_DATA_SYNC_ID = Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, "saved_data_sync");

	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(CoreUtilitiesMod.MOD_ID).versioned(PROTOCOL_VERSION);
		registrar.playBidirectional(MenuUpdatePayload.TYPE, MenuUpdatePayload.CODEC, NeoForgeNetworking::handleMenuUpdateClient, NeoForgeNetworking::handleMenuUpdateServer);
		registrar.playToClient(SavedDataSyncPayload.TYPE, SavedDataSyncPayload.CODEC, NeoForgeNetworking::handleSavedDataSync);
	}

	public record MenuUpdatePayload(int elementType, String name, Object elementState) implements CustomPacketPayload {
		public static final Type<MenuUpdatePayload> TYPE = new Type<>(MENU_UPDATE_ID);
		public static final StreamCodec<FriendlyByteBuf, MenuUpdatePayload> CODEC = StreamCodec.of(MenuUpdatePayload::encode, MenuUpdatePayload::decode);

		private static void encode(FriendlyByteBuf buf, MenuUpdatePayload payload) {
			buf.writeInt(payload.elementType);
			buf.writeUtf(payload.name);
			if (payload.elementType == 0) {
				buf.writeUtf((String) payload.elementState);
			} else if (payload.elementType == 1) {
				buf.writeBoolean((Boolean) payload.elementState);
			}
		}

		private static MenuUpdatePayload decode(FriendlyByteBuf buf) {
			int elementType = buf.readInt();
			String name = buf.readUtf();
			Object state = null;
			if (elementType == 0) {
				state = buf.readUtf();
			} else if (elementType == 1) {
				state = buf.readBoolean();
			}
			return new MenuUpdatePayload(elementType, name, state);
		}

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record SavedDataSyncPayload(int dataType, CompoundTag data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncPayload> TYPE = new Type<>(SAVED_DATA_SYNC_ID);
		public static final StreamCodec<FriendlyByteBuf, SavedDataSyncPayload> CODEC = StreamCodec.of(
				(buf, payload) -> {
					buf.writeInt(payload.dataType);
					buf.writeNbt(payload.data);
				},
				buf -> new SavedDataSyncPayload(buf.readInt(), buf.readNbt()));

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	private static void handleMenuUpdateServer(MenuUpdatePayload payload, IPayloadContext context) {
		if (payload.name.length() > 256 || payload.elementState instanceof String string && string.length() > 8192) {
			return;
		}
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof MenuAccessor menu) {
				menu.getMenuState().put(payload.elementType + ":" + payload.name, payload.elementState);
			}
		});
	}

	private static void handleMenuUpdateClient(MenuUpdatePayload payload, IPayloadContext context) {
		if (payload.name.length() > 256 || payload.elementState instanceof String string && string.length() > 8192) {
			return;
		}
		context.enqueueWork(() -> {
			if (Minecraft.getInstance().gui.screen() instanceof ScreenAccessor accessor) {
				accessor.updateMenuState(payload.elementType, payload.name, payload.elementState);
			}
		});
	}

	private static void handleSavedDataSync(SavedDataSyncPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (payload.data == null) {
				return;
			}
			if (payload.dataType == 0) {
				CoreUtilitiesModVariables.setClientMapVariables(CoreUtilitiesModVariables.MapVariables.load(payload.data));
			} else if (payload.dataType == 1) {
				CoreUtilitiesModVariables.setClientWorldVariables(CoreUtilitiesModVariables.WorldVariables.load(payload.data));
			}
		});
	}

	public static void syncMapData(net.minecraft.world.level.LevelAccessor world, CoreUtilitiesModVariables.MapVariables data) {
		data.setDirty();
		if (world instanceof net.minecraft.world.level.Level level && !level.isClientSide()) {
			PacketDistributor.sendToAllPlayers(new SavedDataSyncPayload(0, data.save(new CompoundTag())));
		}
	}

	@EventBusSubscriber(modid = CoreUtilitiesMod.MOD_ID)
	public static class EventHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity().level().isClientSide()) {
				return;
			}
			if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
				return;
			}
			var mapData = CoreUtilitiesModVariables.MapVariables.get(event.getEntity().level());
			var worldData = CoreUtilitiesModVariables.WorldVariables.get(event.getEntity().level());
			if (mapData != null) {
				PacketDistributor.sendToPlayer(serverPlayer, new SavedDataSyncPayload(0, mapData.save(new CompoundTag())));
			}
			if (worldData != null) {
				PacketDistributor.sendToPlayer(serverPlayer, new SavedDataSyncPayload(1, worldData.save(new CompoundTag())));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity().level().isClientSide()) {
				return;
			}
			if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
				return;
			}
			var worldData = CoreUtilitiesModVariables.WorldVariables.get(event.getEntity().level());
			if (worldData != null) {
				PacketDistributor.sendToPlayer(serverPlayer, new SavedDataSyncPayload(1, worldData.save(new CompoundTag())));
			}
		}
	}
}
