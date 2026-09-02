package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.block.entity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CoreUtilitiesModBlockEntitiesForge {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CoreUtilitiesMod.MOD_ID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> CABLE = register("cable", CoreUtilitiesModBlocksForge.CABLE, CableBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableEBlockEntity>> CABLE_E = register("cable_e", CoreUtilitiesModBlocksForge.CABLE_E, CableEBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableIBlockEntity>> CABLE_I = register("cable_i", CoreUtilitiesModBlocksForge.CABLE_I, CableIBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableLBlockEntity>> CABLE_L = register("cable_l", CoreUtilitiesModBlocksForge.CABLE_L, CableLBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableLCBlockEntity>> CABLE_LC = register("cable_lc", CoreUtilitiesModBlocksForge.CABLE_LC, CableLCBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableLCCBlockEntity>> CABLE_LCC = register("cable_lcc", CoreUtilitiesModBlocksForge.CABLE_LCC, CableLCCBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableLTBlockEntity>> CABLE_LT = register("cable_lt", CoreUtilitiesModBlocksForge.CABLE_LT, CableLTBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableLTCBlockEntity>> CABLE_LTC = register("cable_ltc", CoreUtilitiesModBlocksForge.CABLE_LTC, CableLTCBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableTBlockEntity>> CABLE_T = register("cable_t", CoreUtilitiesModBlocksForge.CABLE_T, CableTBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableTCBlockEntity>> CABLE_TC = register("cable_tc", CoreUtilitiesModBlocksForge.CABLE_TC, CableTCBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableTXBlockEntity>> CABLE_TX = register("cable_tx", CoreUtilitiesModBlocksForge.CABLE_TX, CableTXBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableTXCBlockEntity>> CABLE_TXC = register("cable_txc", CoreUtilitiesModBlocksForge.CABLE_TXC, CableTXCBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableTXCCBlockEntity>> CABLE_TXCC = register("cable_txcc", CoreUtilitiesModBlocksForge.CABLE_TXCC, CableTXCCBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableXBlockEntity>> CABLE_X = register("cable_x", CoreUtilitiesModBlocksForge.CABLE_X, CableXBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableXCBlockEntity>> CABLE_XC = register("cable_xc", CoreUtilitiesModBlocksForge.CABLE_XC, CableXCBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableFBlockEntity>> CABLE_F = register("cable_f", CoreUtilitiesModBlocksForge.CABLE_F, CableFBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableSBlockEntity>> CABLE_S = register("cable_s", CoreUtilitiesModBlocksForge.CABLE_S, CableSBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL = register("solar_panel", CoreUtilitiesModBlocksForge.SOLAR_PANEL, SolarPanelBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanel1BlockEntity>> SOLAR_PANEL_1 = register("solar_panel_1", CoreUtilitiesModBlocksForge.SOLAR_PANEL_1, SolarPanel1BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanel2BlockEntity>> SOLAR_PANEL_2 = register("solar_panel_2", CoreUtilitiesModBlocksForge.SOLAR_PANEL_2, SolarPanel2BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier21BlockEntity>> SOLAR_PANEL_TIER_21 = register("solar_panel_tier_21", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_21, SolarPanelTier21BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier22BlockEntity>> SOLAR_PANEL_TIER_22 = register("solar_panel_tier_22", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_22, SolarPanelTier22BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier23BlockEntity>> SOLAR_PANEL_TIER_23 = register("solar_panel_tier_23", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_23, SolarPanelTier23BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier31BlockEntity>> SOLAR_PANEL_TIER_31 = register("solar_panel_tier_31", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_31, SolarPanelTier31BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier32BlockEntity>> SOLAR_PANEL_TIER_32 = register("solar_panel_tier_32", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_32, SolarPanelTier32BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier33BlockEntity>> SOLAR_PANEL_TIER_33 = register("solar_panel_tier_33", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_33, SolarPanelTier33BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier41BlockEntity>> SOLAR_PANEL_TIER_41 = register("solar_panel_tier_41", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_41, SolarPanelTier41BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier42BlockEntity>> SOLAR_PANEL_TIER_42 = register("solar_panel_tier_42", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_42, SolarPanelTier42BlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelTier43BlockEntity>> SOLAR_PANEL_TIER_43 = register("solar_panel_tier_43", CoreUtilitiesModBlocksForge.SOLAR_PANEL_TIER_43, SolarPanelTier43BlockEntity::new);
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	static {
		CoreUtilitiesModBlockEntities.CABLE = () -> CABLE.get();
		CoreUtilitiesModBlockEntities.CABLE_E = () -> CABLE_E.get();
		CoreUtilitiesModBlockEntities.CABLE_I = () -> CABLE_I.get();
		CoreUtilitiesModBlockEntities.CABLE_L = () -> CABLE_L.get();
		CoreUtilitiesModBlockEntities.CABLE_LC = () -> CABLE_LC.get();
		CoreUtilitiesModBlockEntities.CABLE_LCC = () -> CABLE_LCC.get();
		CoreUtilitiesModBlockEntities.CABLE_LT = () -> CABLE_LT.get();
		CoreUtilitiesModBlockEntities.CABLE_LTC = () -> CABLE_LTC.get();
		CoreUtilitiesModBlockEntities.CABLE_T = () -> CABLE_T.get();
		CoreUtilitiesModBlockEntities.CABLE_TC = () -> CABLE_TC.get();
		CoreUtilitiesModBlockEntities.CABLE_TX = () -> CABLE_TX.get();
		CoreUtilitiesModBlockEntities.CABLE_TXC = () -> CABLE_TXC.get();
		CoreUtilitiesModBlockEntities.CABLE_TXCC = () -> CABLE_TXCC.get();
		CoreUtilitiesModBlockEntities.CABLE_X = () -> CABLE_X.get();
		CoreUtilitiesModBlockEntities.CABLE_XC = () -> CABLE_XC.get();
		CoreUtilitiesModBlockEntities.CABLE_F = () -> CABLE_F.get();
		CoreUtilitiesModBlockEntities.CABLE_S = () -> CABLE_S.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL = () -> SOLAR_PANEL.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_1 = () -> SOLAR_PANEL_1.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_2 = () -> SOLAR_PANEL_2.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_21 = () -> SOLAR_PANEL_TIER_21.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_22 = () -> SOLAR_PANEL_TIER_22.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_23 = () -> SOLAR_PANEL_TIER_23.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_31 = () -> SOLAR_PANEL_TIER_31.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_32 = () -> SOLAR_PANEL_TIER_32.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_33 = () -> SOLAR_PANEL_TIER_33.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_41 = () -> SOLAR_PANEL_TIER_41.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_42 = () -> SOLAR_PANEL_TIER_42.get();
		CoreUtilitiesModBlockEntities.SOLAR_PANEL_TIER_43 = () -> SOLAR_PANEL_TIER_43.get();
	}
}



