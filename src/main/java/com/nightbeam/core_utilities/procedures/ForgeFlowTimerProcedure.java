package com.nightbeam.core_utilities.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

import com.nightbeam.core_utilities.network.CoreUtilitiesModVariables;

@Mod.EventBusSubscriber
public class ForgeFlowTimerProcedure {
	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.level);
		}
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		if (!world.isClientSide()) {
			if (CoreUtilitiesModVariables.MapVariables.get(world).forgeFlow > 0) {
				CoreUtilitiesModVariables.MapVariables.get(world).forgeFlow = CoreUtilitiesModVariables.MapVariables.get(world).forgeFlow - 1;
				CoreUtilitiesModVariables.MapVariables.get(world).syncData(world);
			} else {
				CoreUtilitiesModVariables.MapVariables.get(world).forgeFlow = 300;
				CoreUtilitiesModVariables.MapVariables.get(world).syncData(world);
			}
		}
	}
}