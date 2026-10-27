package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.conduit.EnergyCableBlockEntity;
import com.nightbeam.core_utilities.conduit.ItemPipeBlockEntity;
import com.nightbeam.core_utilities.machine.*;
import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CoreUtilitiesModBlockEntities {
	public static Supplier<BlockEntityType<EnergyCableBlockEntity>> CABLE;
	public static Supplier<BlockEntityType<ItemPipeBlockEntity>> ITEM_PIPE;
	public static Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL;
	public static Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_TIER_21;
	public static Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_TIER_31;
	public static Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_TIER_41;
	public static Supplier<BlockEntityType<MachineBlockEntity>> COMBUSTION_GENERATOR;
	public static Supplier<BlockEntityType<MachineBlockEntity>> GEOTHERMAL_GENERATOR;
	public static Supplier<BlockEntityType<MachineBlockEntity>> ENERGY_CELL_BASIC;
	public static Supplier<BlockEntityType<MachineBlockEntity>> ENERGY_CELL_ADVANCED;
	public static Supplier<BlockEntityType<MachineBlockEntity>> ENERGY_CELL_ELITE;
	public static Supplier<BlockEntityType<MachineBlockEntity>> ENERGY_CELL_CREATIVE;
	public static Supplier<BlockEntityType<MachineBlockEntity>> ELECTRIC_FURNACE;
	public static Supplier<BlockEntityType<MachineBlockEntity>> CRUSHER;
	public static Supplier<BlockEntityType<MachineBlockEntity>> ALLOY_SMELTER;
	public static Supplier<BlockEntityType<MachineBlockEntity>> COMPRESSOR;
	public static Supplier<BlockEntityType<MachineBlockEntity>> QUARRY;
	public static Supplier<BlockEntityType<MachineBlockEntity>> CROP_HARVESTER;
	public static Supplier<BlockEntityType<MachineBlockEntity>> BLOCK_BREAKER;
	public static Supplier<BlockEntityType<MachineBlockEntity>> BLOCK_PLACER;
	public static Supplier<BlockEntityType<MachineBlockEntity>> MOB_GRINDER;

	@SafeVarargs
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name, java.util.function.BiFunction<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState, T> factory, Supplier<Block>... blocks) {
		return Services.PLATFORM.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, () -> {
			Block[] valid = new Block[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				valid[i] = blocks[i].get();
			}
			return Services.PLATFORM.createBlockEntityType(factory, valid);
		});
	}

	public static void register() {
		CABLE = register("cable", (pos, state) -> new EnergyCableBlockEntity(CABLE.get(), pos, state), CoreUtilitiesModBlocks.CABLE, CoreUtilitiesModBlocks.CABLE_ADVANCED, CoreUtilitiesModBlocks.CABLE_ELITE);
		ITEM_PIPE = register("item_pipe", (pos, state) -> new ItemPipeBlockEntity(ITEM_PIPE.get(), pos, state), CoreUtilitiesModBlocks.ITEM_PIPE, CoreUtilitiesModBlocks.RAPID_ITEM_PIPE);
		SOLAR_PANEL = register("solar_panel", (pos, state) -> new SolarPanelBlockEntity(SOLAR_PANEL.get(), pos, state, 1), CoreUtilitiesModBlocks.SOLAR_PANEL);
		SOLAR_PANEL_TIER_21 = register("solar_panel_tier_21", (pos, state) -> new SolarPanelBlockEntity(SOLAR_PANEL_TIER_21.get(), pos, state, 2), CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_21);
		SOLAR_PANEL_TIER_31 = register("solar_panel_tier_31", (pos, state) -> new SolarPanelBlockEntity(SOLAR_PANEL_TIER_31.get(), pos, state, 3), CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_31);
		SOLAR_PANEL_TIER_41 = register("solar_panel_tier_41", (pos, state) -> new SolarPanelBlockEntity(SOLAR_PANEL_TIER_41.get(), pos, state, 4), CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_41);
		COMBUSTION_GENERATOR = register("combustion_generator", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.COMBUSTION_GENERATOR.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.COMBUSTION_GENERATOR);
		GEOTHERMAL_GENERATOR = register("geothermal_generator", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.GEOTHERMAL_GENERATOR.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.GEOTHERMAL_GENERATOR);
		ENERGY_CELL_BASIC = register("energy_cell_basic", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.ENERGY_CELL_BASIC.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.ENERGY_CELL_BASIC);
		ENERGY_CELL_ADVANCED = register("energy_cell_advanced", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.ENERGY_CELL_ADVANCED.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.ENERGY_CELL_ADVANCED);
		ENERGY_CELL_ELITE = register("energy_cell_elite", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.ENERGY_CELL_ELITE.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.ENERGY_CELL_ELITE);
		ENERGY_CELL_CREATIVE = register("energy_cell_creative", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.ENERGY_CELL_CREATIVE.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.ENERGY_CELL_CREATIVE);
		ELECTRIC_FURNACE = register("electric_furnace", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.ELECTRIC_FURNACE.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.ELECTRIC_FURNACE);
		CRUSHER = register("crusher", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.CRUSHER.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.CRUSHER);
		ALLOY_SMELTER = register("alloy_smelter", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.ALLOY_SMELTER.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.ALLOY_SMELTER);
		COMPRESSOR = register("compressor", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.COMPRESSOR.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.COMPRESSOR);
		QUARRY = register("quarry", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.QUARRY.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.QUARRY);
		CROP_HARVESTER = register("crop_harvester", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.CROP_HARVESTER.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.CROP_HARVESTER);
		BLOCK_BREAKER = register("block_breaker", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.BLOCK_BREAKER.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.BLOCK_BREAKER);
		BLOCK_PLACER = register("block_placer", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.BLOCK_PLACER.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.BLOCK_PLACER);
		MOB_GRINDER = register("mob_grinder", (pos, state) -> (MachineBlockEntity) ((MachineBlock) CoreUtilitiesModBlocks.MOB_GRINDER.get()).newBlockEntity(pos, state), CoreUtilitiesModBlocks.MOB_GRINDER);
	}
}
