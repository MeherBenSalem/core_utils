package com.nightbeam.core_utilities;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntitiesForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocksForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModItemsForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModMenusForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModTabs;
import com.nightbeam.core_utilities.neoforge.NeoForgeNetworking;
import com.nightbeam.core_utilities.procedures.ForgeFlowTimerProcedure;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(CoreUtilitiesMod.MOD_ID)
public class CoreUtilitiesModNeoForge {
	public static final Logger LOGGER = LogManager.getLogger(CoreUtilitiesModNeoForge.class);

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public CoreUtilitiesModNeoForge(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(this);
		CoreUtilitiesModBlocksForge.REGISTRY.register(modEventBus);
		CoreUtilitiesModBlockEntitiesForge.REGISTRY.register(modEventBus);
		CoreUtilitiesModItemsForge.REGISTRY.register(modEventBus);
		CoreUtilitiesModTabs.REGISTRY.register(modEventBus);
		CoreUtilitiesModMenusForge.REGISTRY.register(modEventBus);
		modEventBus.addListener(NeoForgeNetworking::register);
	}

	public static void queueServerWork(int tick, Runnable action) {
		workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(ServerTickEvent.Post event) {
		List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
		workQueue.forEach(work -> {
			work.setValue(work.getValue() - 1);
			if (work.getValue() == 0) {
				actions.add(work);
			}
		});
		actions.forEach(e -> e.getKey().run());
		workQueue.removeAll(actions);
	}

	@SubscribeEvent
	public void onWorldTick(LevelTickEvent.Post event) {
		ForgeFlowTimerProcedure.execute(event.getLevel());
	}
}
