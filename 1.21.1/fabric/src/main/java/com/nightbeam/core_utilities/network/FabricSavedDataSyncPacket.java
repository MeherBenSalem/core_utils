package com.nightbeam.core_utilities.network;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FabricSavedDataSyncPacket(int dataType, CompoundTag data) implements CustomPacketPayload {
	public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, "saved_data_sync");
	public static final Type<FabricSavedDataSyncPacket> TYPE = new Type<>(ID_LOCATION);

	public static final StreamCodec<FriendlyByteBuf, FabricSavedDataSyncPacket> CODEC = StreamCodec.of(
			(buf, payload) -> {
				buf.writeInt(payload.dataType);
				buf.writeNbt(payload.data);
			},
			buf -> new FabricSavedDataSyncPacket(buf.readInt(), buf.readNbt()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
