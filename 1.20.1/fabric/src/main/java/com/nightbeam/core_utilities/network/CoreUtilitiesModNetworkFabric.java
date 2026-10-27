package com.nightbeam.core_utilities.network;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

public final class CoreUtilitiesModNetworkFabric {
	public static final ResourceLocation SAVED_DATA_SYNC_ID = new ResourceLocation(CoreUtilitiesMod.MOD_ID, "saved_data_sync");

	private CoreUtilitiesModNetworkFabric() {
	}

	public static void register() {
		FabricMenuStateUpdatePacket.register();
		ServerPlayNetworking.registerGlobalReceiver(SAVED_DATA_SYNC_ID, (server, player, handler, buf, responseSender) -> {
			int type = buf.readInt();
			CompoundTag nbt = buf.readNbt();
			server.execute(() -> {
				if (nbt == null) {
					return;
				}
				if (type == 0) {
					CoreUtilitiesModVariables.setClientMapVariables(CoreUtilitiesModVariables.MapVariables.load(nbt));
				} else {
					CoreUtilitiesModVariables.setClientWorldVariables(CoreUtilitiesModVariables.WorldVariables.load(nbt));
				}
			});
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			SavedData mapdata = CoreUtilitiesModVariables.MapVariables.get(player.level());
			SavedData worlddata = CoreUtilitiesModVariables.WorldVariables.get(player.level());
			if (mapdata instanceof CoreUtilitiesModVariables.MapVariables mapVars) {
				sendSavedData(player, 0, mapVars.save(new CompoundTag()));
			}
			if (worlddata instanceof CoreUtilitiesModVariables.WorldVariables worldVars) {
				sendSavedData(player, 1, worldVars.save(new CompoundTag()));
			}
		});
	}

	/** Client receivers; only called from the client initializer (ClientPlayNetworking does not exist on a dedicated server). */
	public static void registerClient() {
		FabricMenuStateUpdatePacket.registerClient();
		ClientPlayNetworking.registerGlobalReceiver(SAVED_DATA_SYNC_ID, (client, handler, buf, responseSender) -> {
			int type = buf.readInt();
			CompoundTag nbt = buf.readNbt();
			client.execute(() -> {
				if (nbt == null) {
					return;
				}
				if (type == 0) {
					CoreUtilitiesModVariables.setClientMapVariables(CoreUtilitiesModVariables.MapVariables.load(nbt));
				} else {
					CoreUtilitiesModVariables.setClientWorldVariables(CoreUtilitiesModVariables.WorldVariables.load(nbt));
				}
			});
		});
	}

	public static void syncMapData(LevelAccessor world, CoreUtilitiesModVariables.MapVariables data) {
		data.setDirty();
		if (world instanceof net.minecraft.world.level.Level level && !level.isClientSide()) {
			FriendlyByteBuf buf = PacketByteBufs.create();
			buf.writeInt(0);
			buf.writeNbt(data.save(new CompoundTag()));
			level.getServer().getPlayerList().getPlayers().forEach(player -> ServerPlayNetworking.send(player, SAVED_DATA_SYNC_ID, PacketByteBufs.copy(buf)));
		}
	}

	private static void sendSavedData(ServerPlayer player, int type, CompoundTag nbt) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeInt(type);
		buf.writeNbt(nbt);
		ServerPlayNetworking.send(player, SAVED_DATA_SYNC_ID, buf);
	}
}
