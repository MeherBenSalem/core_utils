package com.nightbeam.core_utilities.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

public final class CoreUtilitiesModNetworkFabric {
	private CoreUtilitiesModNetworkFabric() {
	}

	public static void register() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			HolderLookup.Provider registries = player.registryAccess();
			SavedData mapdata = CoreUtilitiesModVariables.MapVariables.get(player.level());
			SavedData worlddata = CoreUtilitiesModVariables.WorldVariables.get(player.level());
			if (mapdata instanceof CoreUtilitiesModVariables.MapVariables mapVars) {
				sendSavedData(player, 0, mapVars.save(new CompoundTag(), registries));
			}
			if (worlddata instanceof CoreUtilitiesModVariables.WorldVariables worldVars) {
				sendSavedData(player, 1, worldVars.save(new CompoundTag(), registries));
			}
		});
	}

	public static void syncMapData(LevelAccessor world, CoreUtilitiesModVariables.MapVariables data) {
		data.setDirty();
		if (world instanceof net.minecraft.world.level.Level level && !level.isClientSide()) {
			CompoundTag nbt = data.save(new CompoundTag(), level.registryAccess());
			level.getServer().getPlayerList().getPlayers().forEach(player -> sendSavedData(player, 0, nbt));
		}
	}

	private static void sendSavedData(ServerPlayer player, int type, CompoundTag nbt) {
		ServerPlayNetworking.send(player, new FabricSavedDataSyncPacket(type, nbt));
	}
}
