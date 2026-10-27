package com.nightbeam.core_utilities;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import com.nightbeam.core_utilities.init.CoreUtilitiesModItemsForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModMenusForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModTabs;
import com.nightbeam.core_utilities.machine.MachineBlockEntity;
import com.nightbeam.core_utilities.neoforge.NeoForgeNetworking;
import com.nightbeam.core_utilities.platform.NeoForgeEnergyStorage;
import com.nightbeam.core_utilities.platform.NeoForgePlatformHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

@Mod(CoreUtilitiesMod.MOD_ID)
public class CoreUtilitiesModNeoForge {
	public static final Logger LOGGER = LogManager.getLogger(CoreUtilitiesModNeoForge.class);

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public CoreUtilitiesModNeoForge(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(this);
		CoreUtilitiesMod.init();
		NeoForgePlatformHelper.attachRegisters(modEventBus);
		CoreUtilitiesModItemsForge.REGISTRY.register(modEventBus);
		CoreUtilitiesModTabs.REGISTRY.register(modEventBus);
		CoreUtilitiesModMenusForge.REGISTRY.register(modEventBus);
		modEventBus.addListener(NeoForgeNetworking::register);
		modEventBus.addListener(CoreUtilitiesModNeoForge::registerCapabilities);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		List<Supplier<? extends BlockEntityType<? extends BlockEntity>>> energyTypes = List.of(
				CoreUtilitiesModBlockEntities.CABLE, CoreUtilitiesModBlockEntities.SOLAR_PANEL, CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_21,
				CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_31, CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_41,
				CoreUtilitiesModBlockEntities.COMBUSTION_GENERATOR, CoreUtilitiesModBlockEntities.GEOTHERMAL_GENERATOR,
				CoreUtilitiesModBlockEntities.ENERGY_CELL_BASIC, CoreUtilitiesModBlockEntities.ENERGY_CELL_ADVANCED, CoreUtilitiesModBlockEntities.ENERGY_CELL_ELITE,
				CoreUtilitiesModBlockEntities.ENERGY_CELL_CREATIVE, CoreUtilitiesModBlockEntities.ELECTRIC_FURNACE, CoreUtilitiesModBlockEntities.CRUSHER,
				CoreUtilitiesModBlockEntities.ALLOY_SMELTER, CoreUtilitiesModBlockEntities.COMPRESSOR, CoreUtilitiesModBlockEntities.QUARRY,
				CoreUtilitiesModBlockEntities.CROP_HARVESTER, CoreUtilitiesModBlockEntities.BLOCK_BREAKER, CoreUtilitiesModBlockEntities.BLOCK_PLACER,
				CoreUtilitiesModBlockEntities.MOB_GRINDER);
		for (Supplier<? extends BlockEntityType<? extends BlockEntity>> type : energyTypes) {
			event.registerBlockEntity(Capabilities.Energy.BLOCK, type.get(), (entity, side) -> {
				if (entity instanceof EnergyHandlerProvider provider) {
					EnergyHandler handler = provider.getEnergyHandler(side);
					return handler == null ? null : NeoForgeEnergyStorage.expose(handler);
				}
				return null;
			});
			event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(), (entity, side) -> {
				if (entity instanceof MachineBlockEntity machine && machine.getContainerSize() > 0) {
					return side == null ? VanillaContainerWrapper.of(machine) : new WorldlyContainerWrapper(machine, side);
				}
				return null;
			});
		}
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
}
