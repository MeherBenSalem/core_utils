package com.nightbeam.core_utilities.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class SavedDataSyncMessage {
	private final int type;
	private CoreUtilitiesModVariables.MapVariables mapData;
	private CoreUtilitiesModVariables.WorldVariables worldData;

	public SavedDataSyncMessage(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		CompoundTag nbt = buffer.readNbt();
		if (nbt != null) {
			if (this.type == 0) {
				this.mapData = CoreUtilitiesModVariables.MapVariables.load(nbt);
			} else {
				this.worldData = CoreUtilitiesModVariables.WorldVariables.load(nbt);
			}
		}
	}

	public SavedDataSyncMessage(int type, CoreUtilitiesModVariables.MapVariables mapData) {
		this.type = type;
		this.mapData = mapData;
	}

	public SavedDataSyncMessage(int type, CoreUtilitiesModVariables.WorldVariables worldData) {
		this.type = type;
		this.worldData = worldData;
	}

	public static void buffer(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		if (message.type == 0 && message.mapData != null) {
			buffer.writeNbt(message.mapData.save(new CompoundTag()));
		} else if (message.type == 1 && message.worldData != null) {
			buffer.writeNbt(message.worldData.save(new CompoundTag()));
		}
	}

	public static void handler(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			if (!context.getDirection().getReceptionSide().isServer()) {
				if (message.type == 0 && message.mapData != null) {
					CoreUtilitiesModVariables.setClientMapVariables(message.mapData);
				} else if (message.type == 1 && message.worldData != null) {
					CoreUtilitiesModVariables.setClientWorldVariables(message.worldData);
				}
			}
		});
		context.setPacketHandled(true);
	}
}
