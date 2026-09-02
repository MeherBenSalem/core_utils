package com.nightbeam.core_utilities.procedures;

import com.nightbeam.core_utilities.util.BlockEntityData;


import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;


import com.nightbeam.core_utilities.network.CoreUtilitiesModVariables;
import com.nightbeam.core_utilities.platform.Services;

public class CableEnergyTransferLogic {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		boolean blockSouth = false;
		boolean blockNorth = false;
		boolean blockEast = false;
		boolean blockWest = false;
		boolean blockUp = false;
		boolean blockDown = false;
		double extractEast = 0;
		double sendUp = 0;
		double energyLoss = 0;
		double sendSouth = 0;
		double sendWest = 0;
		double extractSouth = 0;
		double extractUp = 0;
		double extractNorth = 0;
		double sendDown = 0;
		double extractDown = 0;
		double sideAmount = 0;
		double sendEast = 0;
		double extractWest = 0;
		double sendNorth = 0;
		double sides = 0;
		String nbtNorthTagName = "";
		String nbtUpTagName = "";
		String nbtDownTagName = "";
		String nbtSouthTagName = "";
		String nbtWestTagName = "";
		String nbtEastTagName = "";
		if (!world.isClientSide()) {
			energyLoss = 0;
			nbtNorthTagName = "cablesFCopperNorth";
			nbtEastTagName = "cablesFCopperEast";
			nbtSouthTagName = "cablesFCopperSouth";
			nbtWestTagName = "cablesFCopperWest";
			nbtUpTagName = "cablesFCopperUp";
			nbtDownTagName = "cablesFCopperDown";
			if (CoreUtilitiesModVariables.MapVariables.get(world).forgeFlow == 0) {
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putDouble("forgeFlow", 0);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
			}
			if (Services.ENERGY.canExtractEnergy(world, BlockPos.containing(x, y, z), null) && new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return BlockEntityData.get(blockEntity).getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow") > 0) {
				blockNorth = Services.ENERGY.canReceiveEnergy(world, BlockPos.containing(x, y, z - 1), Direction.SOUTH) && new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x, y, z - 1), nbtSouthTagName) && new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z - 1), "forgeFlow") < new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow");
				blockEast = Services.ENERGY.canReceiveEnergy(world, BlockPos.containing(x + 1, y, z), Direction.WEST) && new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x + 1, y, z), nbtWestTagName) && new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x + 1, y, z), "forgeFlow") < new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow");
				blockSouth = Services.ENERGY.canReceiveEnergy(world, BlockPos.containing(x, y, z + 1), Direction.NORTH) && new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x, y, z + 1), nbtNorthTagName) && new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z + 1), "forgeFlow") < new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow");
				blockWest = Services.ENERGY.canReceiveEnergy(world, BlockPos.containing(x - 1, y, z), Direction.EAST) && new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x - 1, y, z), nbtEastTagName) && new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x - 1, y, z), "forgeFlow") < new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow");
				blockUp = Services.ENERGY.canReceiveEnergy(world, BlockPos.containing(x, y + 1, z), Direction.DOWN) && new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x, y + 1, z), nbtDownTagName) && new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y + 1, z), "forgeFlow") < new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow");
				blockDown = Services.ENERGY.canReceiveEnergy(world, BlockPos.containing(x, y - 1, z), Direction.UP) && new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x, y - 1, z), nbtUpTagName) && new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y - 1, z), "forgeFlow") < new Object() {
					public double getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return BlockEntityData.get(blockEntity).getDouble(tag);
						return -1;
					}
				}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow");
				if (blockNorth) {
					if (!world.isClientSide()) {
						BlockPos _bp = BlockPos.containing(x, y, z - 1);
						BlockEntity _blockEntity = world.getBlockEntity(_bp);
						BlockState _bs = world.getBlockState(_bp);
						if (_blockEntity != null)
							BlockEntityData.get(_blockEntity).putDouble("forgeFlow", ((new Object() {
								public double getValue(LevelAccessor world, BlockPos pos, String tag) {
									BlockEntity blockEntity = world.getBlockEntity(pos);
									if (blockEntity != null)
										return BlockEntityData.get(blockEntity).getDouble(tag);
									return -1;
								}
							}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow")) - 1));
						if (world instanceof Level _level)
							_level.sendBlockUpdated(_bp, _bs, _bs, 3);
					}
				}
				if (blockEast) {
					if (!world.isClientSide()) {
						BlockPos _bp = BlockPos.containing(x + 1, y, z);
						BlockEntity _blockEntity = world.getBlockEntity(_bp);
						BlockState _bs = world.getBlockState(_bp);
						if (_blockEntity != null)
							BlockEntityData.get(_blockEntity).putDouble("forgeFlow", ((new Object() {
								public double getValue(LevelAccessor world, BlockPos pos, String tag) {
									BlockEntity blockEntity = world.getBlockEntity(pos);
									if (blockEntity != null)
										return BlockEntityData.get(blockEntity).getDouble(tag);
									return -1;
								}
							}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow")) - 1));
						if (world instanceof Level _level)
							_level.sendBlockUpdated(_bp, _bs, _bs, 3);
					}
				}
				if (blockSouth) {
					if (!world.isClientSide()) {
						BlockPos _bp = BlockPos.containing(x, y, z + 1);
						BlockEntity _blockEntity = world.getBlockEntity(_bp);
						BlockState _bs = world.getBlockState(_bp);
						if (_blockEntity != null)
							BlockEntityData.get(_blockEntity).putDouble("forgeFlow", ((new Object() {
								public double getValue(LevelAccessor world, BlockPos pos, String tag) {
									BlockEntity blockEntity = world.getBlockEntity(pos);
									if (blockEntity != null)
										return BlockEntityData.get(blockEntity).getDouble(tag);
									return -1;
								}
							}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow")) - 1));
						if (world instanceof Level _level)
							_level.sendBlockUpdated(_bp, _bs, _bs, 3);
					}
				}
				if (blockWest) {
					if (!world.isClientSide()) {
						BlockPos _bp = BlockPos.containing(x - 1, y, z);
						BlockEntity _blockEntity = world.getBlockEntity(_bp);
						BlockState _bs = world.getBlockState(_bp);
						if (_blockEntity != null)
							BlockEntityData.get(_blockEntity).putDouble("forgeFlow", ((new Object() {
								public double getValue(LevelAccessor world, BlockPos pos, String tag) {
									BlockEntity blockEntity = world.getBlockEntity(pos);
									if (blockEntity != null)
										return BlockEntityData.get(blockEntity).getDouble(tag);
									return -1;
								}
							}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow")) - 1));
						if (world instanceof Level _level)
							_level.sendBlockUpdated(_bp, _bs, _bs, 3);
					}
				}
				if (blockUp) {
					if (!world.isClientSide()) {
						BlockPos _bp = BlockPos.containing(x, y + 1, z);
						BlockEntity _blockEntity = world.getBlockEntity(_bp);
						BlockState _bs = world.getBlockState(_bp);
						if (_blockEntity != null)
							BlockEntityData.get(_blockEntity).putDouble("forgeFlow", ((new Object() {
								public double getValue(LevelAccessor world, BlockPos pos, String tag) {
									BlockEntity blockEntity = world.getBlockEntity(pos);
									if (blockEntity != null)
										return BlockEntityData.get(blockEntity).getDouble(tag);
									return -1;
								}
							}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow")) - 1));
						if (world instanceof Level _level)
							_level.sendBlockUpdated(_bp, _bs, _bs, 3);
					}
				}
				if (blockDown) {
					if (!world.isClientSide()) {
						BlockPos _bp = BlockPos.containing(x, y - 1, z);
						BlockEntity _blockEntity = world.getBlockEntity(_bp);
						BlockState _bs = world.getBlockState(_bp);
						if (_blockEntity != null)
							BlockEntityData.get(_blockEntity).putDouble("forgeFlow", ((new Object() {
								public double getValue(LevelAccessor world, BlockPos pos, String tag) {
									BlockEntity blockEntity = world.getBlockEntity(pos);
									if (blockEntity != null)
										return BlockEntityData.get(blockEntity).getDouble(tag);
									return -1;
								}
							}.getValue(world, BlockPos.containing(x, y, z), "forgeFlow")) - 1));
						if (world instanceof Level _level)
							_level.sendBlockUpdated(_bp, _bs, _bs, 3);
					}
				}
				sides = 0;
				if (blockNorth) {
					sides = sides + 1;
				}
				if (blockEast) {
					sides = sides + 1;
				}
				if (blockSouth) {
					sides = sides + 1;
				}
				if (blockWest) {
					sides = sides + 1;
				}
				if (blockUp) {
					sides = sides + 1;
				}
				if (blockDown) {
					sides = sides + 1;
				}
				sideAmount = Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y, z), null) / sides;
				if (blockNorth) {
					extractNorth = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x, y, z - 1), Direction.SOUTH, (int) sideAmount);
					sendNorth = extractNorth - energyLoss;
					if (sendNorth >= 1 && Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y, z - 1), Direction.SOUTH) + sendNorth <= Services.ENERGY.getMaxEnergyStored(world, BlockPos.containing(x, y, z - 1), Direction.SOUTH)) {
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x, y, z - 1), Direction.SOUTH, (int) (Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y, z - 1), Direction.SOUTH) + sendNorth));
						{
							Services.ENERGY.extractEnergy(world, BlockPos.containing(x, y, z), null, (int) extractNorth);
						}
					}
				}
				if (blockEast) {
					extractEast = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x + 1, y, z), Direction.WEST, (int) sideAmount);
					sendEast = extractEast - energyLoss;
					if (sendEast >= 1 && Services.ENERGY.getEnergyStored(world, BlockPos.containing(x + 1, y, z), Direction.WEST) + sendEast <= Services.ENERGY.getMaxEnergyStored(world, BlockPos.containing(x + 1, y, z), Direction.WEST)) {
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x + 1, y, z), Direction.WEST, (int) (Services.ENERGY.getEnergyStored(world, BlockPos.containing(x + 1, y, z), Direction.WEST) + sendEast));
						{
							Services.ENERGY.extractEnergy(world, BlockPos.containing(x, y, z), null, (int) extractEast);
						}
					}
				}
				if (blockSouth) {
					extractSouth = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x, y, z + 1), Direction.NORTH, (int) sideAmount);
					sendSouth = extractSouth - energyLoss;
					if (sendSouth >= 1 && Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y, z + 1), Direction.NORTH) + sendSouth <= Services.ENERGY.getMaxEnergyStored(world, BlockPos.containing(x, y, z + 1), Direction.NORTH)) {
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x, y, z + 1), Direction.NORTH, (int) (Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y, z + 1), Direction.NORTH) + sendSouth));
						{
							Services.ENERGY.extractEnergy(world, BlockPos.containing(x, y, z), null, (int) extractSouth);
						}
					}
				}
				if (blockWest) {
					extractWest = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x - 1, y, z), Direction.EAST, (int) sideAmount);
					sendWest = extractWest - energyLoss;
					if (sendWest >= 1 && Services.ENERGY.getEnergyStored(world, BlockPos.containing(x - 1, y, z), Direction.EAST) + sendWest <= Services.ENERGY.getMaxEnergyStored(world, BlockPos.containing(x - 1, y, z), Direction.EAST)) {
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x - 1, y, z), Direction.EAST, (int) (Services.ENERGY.getEnergyStored(world, BlockPos.containing(x - 1, y, z), Direction.EAST) + sendWest));
						{
							Services.ENERGY.extractEnergy(world, BlockPos.containing(x, y, z), null, (int) extractWest);
						}
					}
				}
				if (blockUp) {
					extractUp = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x, y + 1, z), Direction.DOWN, (int) sideAmount);
					sendUp = extractUp - energyLoss;
					if (sendUp >= 1 && Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y + 1, z), Direction.DOWN) + sendUp <= Services.ENERGY.getMaxEnergyStored(world, BlockPos.containing(x, y + 1, z), Direction.DOWN)) {
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x, y + 1, z), Direction.DOWN, (int) (Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y + 1, z), Direction.DOWN) + sendUp));
						{
							Services.ENERGY.extractEnergy(world, BlockPos.containing(x, y, z), null, (int) extractUp);
						}
					}
				}
				if (blockDown) {
					extractDown = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x, y - 1, z), Direction.UP, (int) sideAmount);
					sendDown = extractDown - energyLoss;
					if (sendDown >= 1 && Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y - 1, z), Direction.UP) + sendDown <= Services.ENERGY.getMaxEnergyStored(world, BlockPos.containing(x, y - 1, z), Direction.UP)) {
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x, y - 1, z), Direction.UP, (int) (Services.ENERGY.getEnergyStored(world, BlockPos.containing(x, y - 1, z), Direction.UP) + sendDown));
						{
							Services.ENERGY.extractEnergy(world, BlockPos.containing(x, y, z), null, (int) extractDown);
						}
					}
				}
			}
		}
	}
}