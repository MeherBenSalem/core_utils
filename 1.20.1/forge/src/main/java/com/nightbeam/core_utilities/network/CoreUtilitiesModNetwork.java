package com.nightbeam.core_utilities.network;

import com.nightbeam.core_utilities.CoreUtilitiesModForge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = com.nightbeam.core_utilities.CoreUtilitiesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreUtilitiesModNetwork {
	public static void register(FMLCommonSetupEvent event) {
		CoreUtilitiesModForge.addNetworkMessage(SavedDataSyncMessage.class, SavedDataSyncMessage::buffer,
				SavedDataSyncMessage::new, SavedDataSyncMessage::handler);
	}

	@Mod.EventBusSubscriber(modid = com.nightbeam.core_utilities.CoreUtilitiesMod.MOD_ID)
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData mapdata = CoreUtilitiesModVariables.MapVariables.get(event.getEntity().level());
				SavedData worlddata = CoreUtilitiesModVariables.WorldVariables.get(event.getEntity().level());
				if (mapdata != null) {
					CoreUtilitiesModForge.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
							new SavedDataSyncMessage(0, (CoreUtilitiesModVariables.MapVariables) mapdata));
				}
				if (worlddata != null) {
					CoreUtilitiesModForge.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
							new SavedDataSyncMessage(1, (CoreUtilitiesModVariables.WorldVariables) worlddata));
				}
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData worlddata = CoreUtilitiesModVariables.WorldVariables.get(event.getEntity().level());
				if (worlddata != null) {
					CoreUtilitiesModForge.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
							new SavedDataSyncMessage(1, (CoreUtilitiesModVariables.WorldVariables) worlddata));
				}
			}
		}
	}

	public static void syncMapData(LevelAccessor world, CoreUtilitiesModVariables.MapVariables data) {
		data.setDirty();
		if (world instanceof net.minecraft.world.level.Level level && !level.isClientSide()) {
			CoreUtilitiesModForge.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, data));
		}
	}
}
