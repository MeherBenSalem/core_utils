package com.nightbeam.core_utilities;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.events.FabricCoreUtilitiesModEvents;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import com.nightbeam.core_utilities.init.CoreUtilitiesModItemsFabric;
import com.nightbeam.core_utilities.init.CoreUtilitiesModMenusFabric;
import com.nightbeam.core_utilities.init.CoreUtilitiesModTabsFabric;
import com.nightbeam.core_utilities.network.CoreUtilitiesModNetworkFabric;
import com.nightbeam.core_utilities.platform.FabricEnergyStorage;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Supplier;

public class CoreUtilitiesModFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		CoreUtilitiesMod.init();
		CoreUtilitiesModItemsFabric.register();
		CoreUtilitiesModMenusFabric.register();
		CoreUtilitiesModTabsFabric.register();
		CoreUtilitiesModNetworkFabric.register();
		FabricCoreUtilitiesModEvents.register();
		registerEnergy();
	}

	private static void registerEnergy() {
		List<Supplier<? extends BlockEntityType<?>>> types = List.of(
				CoreUtilitiesModBlockEntities.CABLE, CoreUtilitiesModBlockEntities.SOLAR_PANEL, CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_21,
				CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_31, CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_41,
				CoreUtilitiesModBlockEntities.COMBUSTION_GENERATOR, CoreUtilitiesModBlockEntities.GEOTHERMAL_GENERATOR,
				CoreUtilitiesModBlockEntities.ENERGY_CELL_BASIC, CoreUtilitiesModBlockEntities.ENERGY_CELL_ADVANCED, CoreUtilitiesModBlockEntities.ENERGY_CELL_ELITE,
				CoreUtilitiesModBlockEntities.ENERGY_CELL_CREATIVE, CoreUtilitiesModBlockEntities.ELECTRIC_FURNACE, CoreUtilitiesModBlockEntities.CRUSHER,
				CoreUtilitiesModBlockEntities.ALLOY_SMELTER, CoreUtilitiesModBlockEntities.COMPRESSOR, CoreUtilitiesModBlockEntities.QUARRY,
				CoreUtilitiesModBlockEntities.CROP_HARVESTER, CoreUtilitiesModBlockEntities.BLOCK_BREAKER, CoreUtilitiesModBlockEntities.BLOCK_PLACER,
				CoreUtilitiesModBlockEntities.MOB_GRINDER);
		for (Supplier<? extends BlockEntityType<?>> type : types) {
			team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((entity, side) -> {
				if (entity instanceof EnergyHandlerProvider provider) {
					EnergyHandler handler = provider.getEnergyHandler(side);
					return handler == null ? null : FabricEnergyStorage.expose(handler);
				}
				return null;
			}, type.get());
		}
	}
}
