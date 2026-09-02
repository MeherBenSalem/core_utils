package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;

public class CableModelUpdaterMainProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (!world.isClientSide()) {
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE.get())) {
				CableModelUpdaterNProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_E.get())) {
				CableModelUpdaterEProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_I.get())) {
				CableModelUpdaterIProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_L.get())) {
				CableModelUpdaterLProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_LC.get())) {
				CableModelUpdaterLCProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_LCC.get())) {
				CableModelUpdaterLCCProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_LT.get())) {
				CableModelUpdaterLTProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_LTC.get())) {
				CableModelUpdaterLTCProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_T.get())) {
				CableModelUpdaterTProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_TC.get())) {
				CableModelUpdaterTCProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_TX.get())) {
				CableModelUpdaterTXProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_TXC.get())) {
				CableModelUpdaterTXCProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_TXCC.get())) {
				CableModelUpdaterTXCCProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_X.get())) {
				CableModelUpdaterXProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_XC.get())) {
				CableModelUpdaterXCProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_F.get())) {
				CableModelUpdaterFProcedure.execute(world, x, y, z);
			}
			if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CoreUtilitiesModBlocks.CABLE_S.get())) {
				CableModelUpdaterSProcedure.execute(world, x, y, z);
			}
		}
	}
}
