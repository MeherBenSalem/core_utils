package com.nightbeam.core_utilities.procedures;



import com.nightbeam.core_utilities.util.TagHelper;
import com.nightbeam.core_utilities.util.BlockEntityData;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.Map;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlocks;

public class CableModelUpdaterLTProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		BlockState blockSelector = Blocks.AIR.defaultBlockState();
		String nbtSouthTagName = "";
		String nbtWestTagName = "";
		String nbtNorthTagName = "";
		String nbtEastTagName = "";
		String nbtUpTagName = "";
		String nbtDownTagName = "";
		String tagPath = "";
		if (!world.isClientSide()) {
			blockSelector = CoreUtilitiesModBlocks.CABLE_LT.get().defaultBlockState();
			tagPath = "core_utilities:cables/f/copper";
			nbtNorthTagName = "cablesFCopperNorth";
			nbtEastTagName = "cablesFCopperEast";
			nbtSouthTagName = "cablesFCopperSouth";
			nbtWestTagName = "cablesFCopperWest";
			nbtUpTagName = "cablesFCopperUp";
			nbtDownTagName = "cablesFCopperDown";
			if (((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y + 1, z)), tagPath)) || new Object() {
				public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
					return false;
				}
			}.getValue(world, BlockPos.containing(x, y + 1, z), nbtDownTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y - 1, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y - 1, z), nbtUpTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z + 1)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z + 1), nbtNorthTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z - 1)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z - 1), nbtSouthTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x + 1, y, z)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x + 1, y, z), nbtWestTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x - 1, y, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x - 1, y, z), nbtEastTagName))) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = blockSelector;
					BlockState _bso = world.getBlockState(_bp);
					for (Property.Value<?> entry : _bso.getValues().toList()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.property().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.value());
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
								_be.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, world.registryAccess(), _bnbt));
							} catch (Exception ignored) {
							}
						}
					}
				}
				{
					Direction _dir = Direction.NORTH;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_property instanceof EnumProperty _dp && _dp.getPossibleValues().contains(_dir)) {
						world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
					} else {
						_property = _bs.getBlock().getStateDefinition().getProperty("axis");
						if (_property instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis()))
							world.setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
					}
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtUpTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtDownTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtNorthTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtEastTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtSouthTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtWestTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
			} else if (((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y + 1, z)), tagPath)) || new Object() {
				public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
					return false;
				}
			}.getValue(world, BlockPos.containing(x, y + 1, z), nbtDownTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y - 1, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y - 1, z), nbtUpTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z + 1)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z + 1), nbtNorthTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z - 1)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z - 1), nbtSouthTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x + 1, y, z)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x + 1, y, z), nbtWestTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x - 1, y, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x - 1, y, z), nbtEastTagName))) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = blockSelector;
					BlockState _bso = world.getBlockState(_bp);
					for (Property.Value<?> entry : _bso.getValues().toList()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.property().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.value());
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
								_be.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, world.registryAccess(), _bnbt));
							} catch (Exception ignored) {
							}
						}
					}
				}
				{
					Direction _dir = Direction.EAST;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_property instanceof EnumProperty _dp && _dp.getPossibleValues().contains(_dir)) {
						world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
					} else {
						_property = _bs.getBlock().getStateDefinition().getProperty("axis");
						if (_property instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis()))
							world.setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
					}
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtUpTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtDownTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtNorthTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtEastTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtSouthTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtWestTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
			} else if (((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y + 1, z)), tagPath)) || new Object() {
				public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
					return false;
				}
			}.getValue(world, BlockPos.containing(x, y + 1, z), nbtDownTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y - 1, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y - 1, z), nbtUpTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z + 1)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z + 1), nbtNorthTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z - 1)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z - 1), nbtSouthTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x + 1, y, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x + 1, y, z), nbtWestTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x - 1, y, z)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x - 1, y, z), nbtEastTagName))) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = blockSelector;
					BlockState _bso = world.getBlockState(_bp);
					for (Property.Value<?> entry : _bso.getValues().toList()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.property().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.value());
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
								_be.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, world.registryAccess(), _bnbt));
							} catch (Exception ignored) {
							}
						}
					}
				}
				{
					Direction _dir = Direction.SOUTH;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_property instanceof EnumProperty _dp && _dp.getPossibleValues().contains(_dir)) {
						world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
					} else {
						_property = _bs.getBlock().getStateDefinition().getProperty("axis");
						if (_property instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis()))
							world.setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
					}
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtUpTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtDownTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtNorthTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtEastTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtSouthTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtWestTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
			} else if (((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y + 1, z)), tagPath)) || new Object() {
				public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
					return false;
				}
			}.getValue(world, BlockPos.containing(x, y + 1, z), nbtDownTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y - 1, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y - 1, z), nbtUpTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z + 1)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z + 1), nbtNorthTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x, y, z - 1)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x, y, z - 1), nbtSouthTagName)) && !(TagHelper.isInTag(world.getBlockState(BlockPos.containing(x + 1, y, z)), tagPath))
					&& !(new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x + 1, y, z), nbtWestTagName))
					&& ((TagHelper.isInTag(world.getBlockState(BlockPos.containing(x - 1, y, z)), tagPath)) || new Object() {
						public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
							BlockEntity blockEntity = world.getBlockEntity(pos);
							if (blockEntity != null)
								return BlockEntityData.get(blockEntity).getBoolean(tag).orElse(false);
							return false;
						}
					}.getValue(world, BlockPos.containing(x - 1, y, z), nbtEastTagName))) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = blockSelector;
					BlockState _bso = world.getBlockState(_bp);
					for (Property.Value<?> entry : _bso.getValues().toList()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.property().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.value());
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
								_be.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, world.registryAccess(), _bnbt));
							} catch (Exception ignored) {
							}
						}
					}
				}
				{
					Direction _dir = Direction.WEST;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_property instanceof EnumProperty _dp && _dp.getPossibleValues().contains(_dir)) {
						world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
					} else {
						_property = _bs.getBlock().getStateDefinition().getProperty("axis");
						if (_property instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis()))
							world.setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
					}
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtUpTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtDownTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtNorthTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtEastTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtSouthTagName, false);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						BlockEntityData.get(_blockEntity).putBoolean(nbtWestTagName, true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
			}
		}
	}
}


