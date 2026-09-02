package com.nightbeam.core_utilities.network;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record FabricMenuStateUpdatePacket(int elementType, String name, Object elementState)
		implements CustomPacketPayload {
	public static final Identifier ID_LOCATION = Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, "menu_state_update");
	public static final Type<FabricMenuStateUpdatePacket> TYPE = new Type<>(ID_LOCATION);

	public static final StreamCodec<FriendlyByteBuf, FabricMenuStateUpdatePacket> CODEC = StreamCodec.of(
			FabricMenuStateUpdatePacket::encode,
			FabricMenuStateUpdatePacket::decode);

	public static void encode(FriendlyByteBuf buf, FabricMenuStateUpdatePacket payload) {
		buf.writeInt(payload.elementType);
		buf.writeUtf(payload.name);
		if (payload.elementType == 0) {
			buf.writeUtf((String) payload.elementState);
		} else if (payload.elementType == 1) {
			buf.writeBoolean((Boolean) payload.elementState);
		}
	}

	public static FabricMenuStateUpdatePacket decode(FriendlyByteBuf buf) {
		int elementType = buf.readInt();
		String name = buf.readUtf();
		Object state = null;
		if (elementType == 0) {
			state = buf.readUtf();
		} else if (elementType == 1) {
			state = buf.readBoolean();
		}
		return new FabricMenuStateUpdatePacket(elementType, name, state);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
