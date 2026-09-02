package com.nightbeam.core_utilities.events;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;
import com.nightbeam.core_utilities.init.MenuAccessor;
import com.nightbeam.core_utilities.init.ScreenAccessor;
import com.nightbeam.core_utilities.network.CoreUtilitiesModVariables;
import com.nightbeam.core_utilities.network.FabricMenuStateUpdatePacket;
import com.nightbeam.core_utilities.network.FabricSavedDataSyncPacket;
import com.nightbeam.core_utilities.procedures.FloatingBlockOnBlockRightClickedProcedure;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public class FabricCoreUtilitiesModEvents {
	public static void register() {
		PayloadTypeRegistry.playC2S().register(FabricMenuStateUpdatePacket.TYPE, FabricMenuStateUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(FabricMenuStateUpdatePacket.TYPE, FabricMenuStateUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(FabricSavedDataSyncPacket.TYPE, FabricSavedDataSyncPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(FabricMenuStateUpdatePacket.TYPE,
				(payload, context) -> context.server().execute(() -> handleMenuStateUpdate(payload, context.player())));

		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() == CoreUtilitiesModBlocks.FLOATING_BLOCK.get().asItem()) {
				BlockPos pos = player.blockPosition();
				FloatingBlockOnBlockRightClickedProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), player);
				return InteractionResultHolder.success(stack);
			}
			return InteractionResultHolder.pass(stack);
		});
	}

	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(FabricMenuStateUpdatePacket.TYPE, (payload, context) -> context.client().execute(() -> {
			if (payload.name().length() > 256 || payload.elementState() instanceof String string && string.length() > 8192) {
				return;
			}
			if (context.player().containerMenu instanceof MenuAccessor menu) {
				menu.getMenuState().put(payload.elementType() + ":" + payload.name(), payload.elementState());
			}
			if (Minecraft.getInstance().screen instanceof ScreenAccessor accessor) {
				accessor.updateMenuState(payload.elementType(), payload.name(), payload.elementState());
			}
		}));
		ClientPlayNetworking.registerGlobalReceiver(FabricSavedDataSyncPacket.TYPE, (payload, context) -> context.client().execute(() -> {
			if (payload.data() == null) {
				return;
			}
			HolderLookup.Provider registries = context.client().level != null
					? context.client().level.registryAccess()
					: context.player().registryAccess();
			if (payload.dataType() == 0) {
				CoreUtilitiesModVariables.setClientMapVariables(CoreUtilitiesModVariables.MapVariables.load(payload.data(), registries));
			} else {
				CoreUtilitiesModVariables.setClientWorldVariables(CoreUtilitiesModVariables.WorldVariables.load(payload.data(), registries));
			}
		}));
	}

	public static void handleMenuStateUpdate(FabricMenuStateUpdatePacket payload, net.minecraft.server.level.ServerPlayer player) {
		if (payload.name().length() > 256 || payload.elementState() instanceof String string && string.length() > 8192) {
			return;
		}
		if (player.containerMenu instanceof MenuAccessor menu) {
			menu.getMenuState().put(payload.elementType() + ":" + payload.name(), payload.elementState());
		}
	}
}
