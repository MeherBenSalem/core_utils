package com.nightbeam.core_utilities.procedures;

import com.nightbeam.core_utilities.util.BlockEntityData;


import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.Map;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;
import com.nightbeam.core_utilities.platform.Services;

public class SolarPanelUpdateTickTier4Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		double send = 0;
		double sendPosition = 0;
		double maxSend = 0;
		BlockState lowLevelBlock = Blocks.AIR.defaultBlockState();
		BlockState mediumLevelBlock = Blocks.AIR.defaultBlockState();
		BlockState highLevelBlock = Blocks.AIR.defaultBlockState();
		if (!world.isClientSide()) {
			sendPosition = y - 1;
			maxSend = 512;
			lowLevelBlock = CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_43.get().defaultBlockState();
			mediumLevelBlock = CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_42.get().defaultBlockState();
			highLevelBlock = CoreUtilitiesModBlocks.SOLAR_PANEL_TIER_41.get().defaultBlockState();
			if (Services.ENERGY.canReceiveEnergy(world, BlockPos.containing(x, sendPosition, z), Direction.UP) && new Object() {
				public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return BlockEntityData.get(blockEntity).getBoolean(tag);
					return false;
				}
			}.getValue(world, BlockPos.containing(x, sendPosition, z), "cablesFCopperUp")) {
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, sendPosition, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putDouble("forgeFlow", 1024);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (world.getLevelData().isRaining() && world.getLevelData().isThundering() && world instanceof Level _lvl6 && _lvl6.isDay()) {
					{
						BlockPos _bp = BlockPos.containing(x, y, z);
						BlockState _bs = lowLevelBlock;
						BlockState _bso = world.getBlockState(_bp);
						for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
							Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
							if (_property != null && _bs.getValue(_property) != null)
								try {
									_bs = _bs.setValue(_property, (Comparable) entry.getValue());
								} catch (Exception e) {
								}
						}
						BlockEntity _be = world.getBlockEntity(_bp);
						CompoundTag _bnbt = null;
						if (_be != null) {
							_bnbt = _be.saveWithFullMetadata(world.registryAccess());
							_be.setRemoved();
						}
						world.setBlock(_bp, _bs, 3);
						if (_bnbt != null) {
							_be = world.getBlockEntity(_bp);
							if (_be != null) {
								try {
									_be.loadWithComponents(_bnbt, world.registryAccess());
								} catch (Exception ignored) {
								}
							}
						}
					}
					send = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x, sendPosition, z), Direction.UP, (int) (maxSend / 4));
					{
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x, sendPosition, z), Direction.UP, (int) send);
					}
				} else if ((!world.getLevelData().isRaining() && world.getLevelData().isThundering() || world.getLevelData().isRaining() && !world.getLevelData().isThundering()) && world instanceof Level _lvl14 && _lvl14.isDay()) {
					{
						BlockPos _bp = BlockPos.containing(x, y, z);
						BlockState _bs = mediumLevelBlock;
						BlockState _bso = world.getBlockState(_bp);
						for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
							Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
							if (_property != null && _bs.getValue(_property) != null)
								try {
									_bs = _bs.setValue(_property, (Comparable) entry.getValue());
								} catch (Exception e) {
								}
						}
						BlockEntity _be = world.getBlockEntity(_bp);
						CompoundTag _bnbt = null;
						if (_be != null) {
							_bnbt = _be.saveWithFullMetadata(world.registryAccess());
							_be.setRemoved();
						}
						world.setBlock(_bp, _bs, 3);
						if (_bnbt != null) {
							_be = world.getBlockEntity(_bp);
							if (_be != null) {
								try {
									_be.loadWithComponents(_bnbt, world.registryAccess());
								} catch (Exception ignored) {
								}
							}
						}
					}
					send = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x, sendPosition, z), Direction.UP, (int) (maxSend / 2));
					{
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x, sendPosition, z), Direction.UP, (int) send);
					}
				} else if (!world.getLevelData().isRaining() && !world.getLevelData().isThundering() && world instanceof Level _lvl20 && _lvl20.isDay()) {
					{
						BlockPos _bp = BlockPos.containing(x, y, z);
						BlockState _bs = highLevelBlock;
						BlockState _bso = world.getBlockState(_bp);
						for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
							Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
							if (_property != null && _bs.getValue(_property) != null)
								try {
									_bs = _bs.setValue(_property, (Comparable) entry.getValue());
								} catch (Exception e) {
								}
						}
						BlockEntity _be = world.getBlockEntity(_bp);
						CompoundTag _bnbt = null;
						if (_be != null) {
							_bnbt = _be.saveWithFullMetadata(world.registryAccess());
							_be.setRemoved();
						}
						world.setBlock(_bp, _bs, 3);
						if (_bnbt != null) {
							_be = world.getBlockEntity(_bp);
							if (_be != null) {
								try {
									_be.loadWithComponents(_bnbt, world.registryAccess());
								} catch (Exception ignored) {
								}
							}
						}
					}
					send = Services.ENERGY.receiveEnergySimulate(world, BlockPos.containing(x, sendPosition, z), Direction.UP, (int) maxSend);
					{
						Services.ENERGY.receiveEnergy(world, BlockPos.containing(x, sendPosition, z), Direction.UP, (int) send);
					}
				}
			}
		}
	}
}