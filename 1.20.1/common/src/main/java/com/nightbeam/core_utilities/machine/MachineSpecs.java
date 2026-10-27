package com.nightbeam.core_utilities.machine;

import java.util.List;

import static com.nightbeam.core_utilities.machine.MachineSpec.Progress.ARROW;
import static com.nightbeam.core_utilities.machine.MachineSpec.Progress.FLAME;
import static com.nightbeam.core_utilities.machine.SlotKind.FUEL;
import static com.nightbeam.core_utilities.machine.SlotKind.INPUT;
import static com.nightbeam.core_utilities.machine.SlotKind.OUTPUT;

/** GUI and inventory layouts of every machine. Coordinates are the slot frame's top-left in the 176x166 GUI. */
public final class MachineSpecs {
	public static final MachineSpec ELECTRIC_FURNACE = new MachineSpec("electric_furnace").slot(INPUT, 50, 34).progress(ARROW, 76, 34).bigSlot(OUTPUT, 108, 30).finish();
	public static final MachineSpec CRUSHER = new MachineSpec("crusher").slot(INPUT, 44, 34).progress(ARROW, 68, 34).bigSlot(OUTPUT, 98, 30).slot(OUTPUT, 126, 34).finish();
	public static final MachineSpec ALLOY_SMELTER = new MachineSpec("alloy_smelter").slot(INPUT, 44, 24).slot(INPUT, 44, 44).progress(ARROW, 70, 34).bigSlot(OUTPUT, 104, 30).finish();
	public static final MachineSpec COMPRESSOR = new MachineSpec("compressor").slot(INPUT, 50, 34).progress(ARROW, 76, 34).bigSlot(OUTPUT, 108, 30).finish();
	public static final MachineSpec COMBUSTION_GENERATOR = new MachineSpec("combustion_generator").slot(FUEL, 79, 52).progress(FLAME, 80, 34).finish();
	public static final MachineSpec GEOTHERMAL_GENERATOR = new MachineSpec("geothermal_generator").noUpgrades().noSideConfig().finish();
	public static final MachineSpec ENERGY_CELL_BASIC = cell("energy_cell_basic");
	public static final MachineSpec ENERGY_CELL_ADVANCED = cell("energy_cell_advanced");
	public static final MachineSpec ENERGY_CELL_ELITE = cell("energy_cell_elite");
	public static final MachineSpec ENERGY_CELL_CREATIVE = cell("energy_cell_creative");
	public static final MachineSpec QUARRY = new MachineSpec("quarry").grid(OUTPUT, 62, 16, 3, 3).finish();
	public static final MachineSpec CROP_HARVESTER = new MachineSpec("crop_harvester").slot(INPUT, 34, 34).grid(OUTPUT, 62, 16, 3, 3).finish();
	public static final MachineSpec BLOCK_BREAKER = new MachineSpec("block_breaker").progress(ARROW, 34, 34).grid(OUTPUT, 62, 16, 3, 3).finish();
	public static final MachineSpec BLOCK_PLACER = new MachineSpec("block_placer").grid(INPUT, 62, 16, 3, 3).finish();
	public static final MachineSpec MOB_GRINDER = new MachineSpec("mob_grinder").grid(OUTPUT, 62, 16, 3, 3).finish();

	public static final List<MachineSpec> ALL = List.of(ELECTRIC_FURNACE, CRUSHER, ALLOY_SMELTER, COMPRESSOR, COMBUSTION_GENERATOR, GEOTHERMAL_GENERATOR,
			ENERGY_CELL_BASIC, ENERGY_CELL_ADVANCED, ENERGY_CELL_ELITE, ENERGY_CELL_CREATIVE, QUARRY, CROP_HARVESTER, BLOCK_BREAKER, BLOCK_PLACER, MOB_GRINDER);

	private MachineSpecs() {
	}

	private static MachineSpec cell(String name) {
		return new MachineSpec(name).noUpgrades().finish();
	}
}
