/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.nightbeam.core_utilities.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;

import com.nightbeam.core_utilities.block.entity.SolarPanelBlockEntity;
import com.nightbeam.core_utilities.block.entity.SolarPanel2BlockEntity;
import com.nightbeam.core_utilities.block.entity.SolarPanel1BlockEntity;
import com.nightbeam.core_utilities.block.entity.CableXCBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableXBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableTXCCBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableTXCBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableTXBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableTCBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableTBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableSBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableLTCBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableLTBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableLCCBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableLCBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableLBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableIBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableFBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableEBlockEntity;
import com.nightbeam.core_utilities.block.entity.CableBlockEntity;
import com.nightbeam.core_utilities.CoreUtilitiesMod;

public class CoreUtilitiesModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoreUtilitiesMod.MODID);
	public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE = register("cable", CoreUtilitiesModBlocks.CABLE, CableBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableEBlockEntity>> CABLE_E = register("cable_e", CoreUtilitiesModBlocks.CABLE_E, CableEBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableIBlockEntity>> CABLE_I = register("cable_i", CoreUtilitiesModBlocks.CABLE_I, CableIBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableLBlockEntity>> CABLE_L = register("cable_l", CoreUtilitiesModBlocks.CABLE_L, CableLBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableLCBlockEntity>> CABLE_LC = register("cable_lc", CoreUtilitiesModBlocks.CABLE_LC, CableLCBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableLCCBlockEntity>> CABLE_LCC = register("cable_lcc", CoreUtilitiesModBlocks.CABLE_LCC, CableLCCBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableLTBlockEntity>> CABLE_LT = register("cable_lt", CoreUtilitiesModBlocks.CABLE_LT, CableLTBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableLTCBlockEntity>> CABLE_LTC = register("cable_ltc", CoreUtilitiesModBlocks.CABLE_LTC, CableLTCBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableTBlockEntity>> CABLE_T = register("cable_t", CoreUtilitiesModBlocks.CABLE_T, CableTBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableTCBlockEntity>> CABLE_TC = register("cable_tc", CoreUtilitiesModBlocks.CABLE_TC, CableTCBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableTXBlockEntity>> CABLE_TX = register("cable_tx", CoreUtilitiesModBlocks.CABLE_TX, CableTXBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableTXCBlockEntity>> CABLE_TXC = register("cable_txc", CoreUtilitiesModBlocks.CABLE_TXC, CableTXCBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableTXCCBlockEntity>> CABLE_TXCC = register("cable_txcc", CoreUtilitiesModBlocks.CABLE_TXCC, CableTXCCBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableXBlockEntity>> CABLE_X = register("cable_x", CoreUtilitiesModBlocks.CABLE_X, CableXBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableXCBlockEntity>> CABLE_XC = register("cable_xc", CoreUtilitiesModBlocks.CABLE_XC, CableXCBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableFBlockEntity>> CABLE_F = register("cable_f", CoreUtilitiesModBlocks.CABLE_F, CableFBlockEntity::new);
	public static final RegistryObject<BlockEntityType<CableSBlockEntity>> CABLE_S = register("cable_s", CoreUtilitiesModBlocks.CABLE_S, CableSBlockEntity::new);
	public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL = register("solar_panel", CoreUtilitiesModBlocks.SOLAR_PANEL, SolarPanelBlockEntity::new);
	public static final RegistryObject<BlockEntityType<SolarPanel1BlockEntity>> SOLAR_PANEL_1 = register("solar_panel_1", CoreUtilitiesModBlocks.SOLAR_PANEL_1, SolarPanel1BlockEntity::new);
	public static final RegistryObject<BlockEntityType<SolarPanel2BlockEntity>> SOLAR_PANEL_2 = register("solar_panel_2", CoreUtilitiesModBlocks.SOLAR_PANEL_2, SolarPanel2BlockEntity::new);

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}