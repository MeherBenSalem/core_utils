package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class VectorPlateWalksOnTheBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity, double power) {
		if (entity == null)
			return;
		if (!entity.isShiftKeyDown()) {
			if ((new Object() {
				public Direction getDirection(BlockState _bs) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_prop instanceof DirectionProperty _dp)
						return _bs.getValue(_dp);
					_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
					return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
				}
			}.getDirection(blockstate)) == Direction.NORTH) {
				if (new Object() {
					public int getContainerSize(LevelAccessor world, BlockPos pos) {
						BlockEntity _ent = world.getBlockEntity(pos);
						if (_ent != null) {
							if (_ent instanceof BaseContainerBlockEntity _block) {
								return _block.getContainerSize();
							}
						}
						return 0;
					}

					public int getAmount(LevelAccessor world, BlockPos pos) {
						Block block = world.getBlockState(pos).getBlock();
						if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
							boolean isSingle = block.getStateDefinition().getProperty("type") instanceof EnumProperty _getep5 && world.getBlockState(pos).getValue(_getep5).toString().equals("SINGLE");
							if (!isSingle) {
								return getContainerSize(world, pos) * 2;
							}
						}
						return getContainerSize(world, pos);
					}
				}.getAmount(world, new BlockPos((int) x, (int) y, (int) (z + 1))) > 0) {
					if (entity instanceof ItemEntity) {
						int insertCount = ((entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).getCount());
						ItemStack stackToInsert = (entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).copy();
						stackToInsert.setCount(1);
						BlockPos pos = new BlockPos((int) (x), (int) (y), (int) ((z + 1)));
						BlockEntity Bentity = world.getBlockEntity(pos);
						if (Bentity instanceof BaseContainerBlockEntity blockInv) {
							int size = blockInv.getContainerSize();
							for (int slot = 0; slot < size && insertCount > 0; slot++) {
								ItemStack slotStack = blockInv.getItem(slot);
								if (slotStack.isEmpty()) {
									int amount = Math.min(insertCount, stackToInsert.getMaxStackSize());
									ItemStack toInsert = stackToInsert.copy();
									toInsert.setCount(amount);
									blockInv.setItem(slot, toInsert);
									insertCount -= amount;
									continue;
								}
								if (ItemStack.isSameItemSameTags(slotStack, stackToInsert) && slotStack.getCount() < slotStack.getMaxStackSize()) {
									int room = slotStack.getMaxStackSize() - slotStack.getCount();
									int amountToAdd = Math.min(room, insertCount);
									slotStack.grow(amountToAdd);
									insertCount -= amountToAdd;
								}
							}
							blockInv.setChanged();
							if (world instanceof net.minecraft.world.level.Level _level)
								_level.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
						}
						if (!entity.level().isClientSide())
							entity.discard();
					}
				} else {
					entity.push(0, 0, (power * 1));
				}
			} else if ((new Object() {
				public Direction getDirection(BlockState _bs) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_prop instanceof DirectionProperty _dp)
						return _bs.getValue(_dp);
					_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
					return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
				}
			}.getDirection(blockstate)) == Direction.EAST) {
				if (new Object() {
					public int getContainerSize(LevelAccessor world, BlockPos pos) {
						BlockEntity _ent = world.getBlockEntity(pos);
						if (_ent != null) {
							if (_ent instanceof BaseContainerBlockEntity _block) {
								return _block.getContainerSize();
							}
						}
						return 0;
					}

					public int getAmount(LevelAccessor world, BlockPos pos) {
						Block block = world.getBlockState(pos).getBlock();
						if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
							boolean isSingle = block.getStateDefinition().getProperty("type") instanceof EnumProperty _getep5 && world.getBlockState(pos).getValue(_getep5).toString().equals("SINGLE");
							if (!isSingle) {
								return getContainerSize(world, pos) * 2;
							}
						}
						return getContainerSize(world, pos);
					}
				}.getAmount(world, new BlockPos((int) (x - 1), (int) y, (int) z)) > 0) {
					if (entity instanceof ItemEntity) {
						int insertCount = ((entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).getCount());
						ItemStack stackToInsert = (entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).copy();
						stackToInsert.setCount(1);
						BlockPos pos = new BlockPos((int) ((x - 1)), (int) (y), (int) (z));
						BlockEntity Bentity = world.getBlockEntity(pos);
						if (Bentity instanceof BaseContainerBlockEntity blockInv) {
							int size = blockInv.getContainerSize();
							for (int slot = 0; slot < size && insertCount > 0; slot++) {
								ItemStack slotStack = blockInv.getItem(slot);
								if (slotStack.isEmpty()) {
									int amount = Math.min(insertCount, stackToInsert.getMaxStackSize());
									ItemStack toInsert = stackToInsert.copy();
									toInsert.setCount(amount);
									blockInv.setItem(slot, toInsert);
									insertCount -= amount;
									continue;
								}
								if (ItemStack.isSameItemSameTags(slotStack, stackToInsert) && slotStack.getCount() < slotStack.getMaxStackSize()) {
									int room = slotStack.getMaxStackSize() - slotStack.getCount();
									int amountToAdd = Math.min(room, insertCount);
									slotStack.grow(amountToAdd);
									insertCount -= amountToAdd;
								}
							}
							blockInv.setChanged();
							if (world instanceof net.minecraft.world.level.Level _level)
								_level.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
						}
						if (!entity.level().isClientSide())
							entity.discard();
					}
				} else {
					entity.push((power * (-1)), 0, 0);
				}
			} else if ((new Object() {
				public Direction getDirection(BlockState _bs) {
					Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
					if (_prop instanceof DirectionProperty _dp)
						return _bs.getValue(_dp);
					_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
					return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
				}
			}.getDirection(blockstate)) == Direction.SOUTH) {
				if (new Object() {
					public int getContainerSize(LevelAccessor world, BlockPos pos) {
						BlockEntity _ent = world.getBlockEntity(pos);
						if (_ent != null) {
							if (_ent instanceof BaseContainerBlockEntity _block) {
								return _block.getContainerSize();
							}
						}
						return 0;
					}

					public int getAmount(LevelAccessor world, BlockPos pos) {
						Block block = world.getBlockState(pos).getBlock();
						if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
							boolean isSingle = block.getStateDefinition().getProperty("type") instanceof EnumProperty _getep5 && world.getBlockState(pos).getValue(_getep5).toString().equals("SINGLE");
							if (!isSingle) {
								return getContainerSize(world, pos) * 2;
							}
						}
						return getContainerSize(world, pos);
					}
				}.getAmount(world, new BlockPos((int) x, (int) y, (int) (z - 1))) > 0) {
					if (entity instanceof ItemEntity) {
						int insertCount = ((entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).getCount());
						ItemStack stackToInsert = (entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).copy();
						stackToInsert.setCount(1);
						BlockPos pos = new BlockPos((int) (x), (int) (y), (int) ((z - 1)));
						BlockEntity Bentity = world.getBlockEntity(pos);
						if (Bentity instanceof BaseContainerBlockEntity blockInv) {
							int size = blockInv.getContainerSize();
							for (int slot = 0; slot < size && insertCount > 0; slot++) {
								ItemStack slotStack = blockInv.getItem(slot);
								if (slotStack.isEmpty()) {
									int amount = Math.min(insertCount, stackToInsert.getMaxStackSize());
									ItemStack toInsert = stackToInsert.copy();
									toInsert.setCount(amount);
									blockInv.setItem(slot, toInsert);
									insertCount -= amount;
									continue;
								}
								if (ItemStack.isSameItemSameTags(slotStack, stackToInsert) && slotStack.getCount() < slotStack.getMaxStackSize()) {
									int room = slotStack.getMaxStackSize() - slotStack.getCount();
									int amountToAdd = Math.min(room, insertCount);
									slotStack.grow(amountToAdd);
									insertCount -= amountToAdd;
								}
							}
							blockInv.setChanged();
							if (world instanceof net.minecraft.world.level.Level _level)
								_level.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
						}
						if (!entity.level().isClientSide())
							entity.discard();
					}
				} else {
					entity.push(0, 0, (power * (-1)));
				}
			} else {
				if (new Object() {
					public int getContainerSize(LevelAccessor world, BlockPos pos) {
						BlockEntity _ent = world.getBlockEntity(pos);
						if (_ent != null) {
							if (_ent instanceof BaseContainerBlockEntity _block) {
								return _block.getContainerSize();
							}
						}
						return 0;
					}

					public int getAmount(LevelAccessor world, BlockPos pos) {
						Block block = world.getBlockState(pos).getBlock();
						if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
							boolean isSingle = block.getStateDefinition().getProperty("type") instanceof EnumProperty _getep5 && world.getBlockState(pos).getValue(_getep5).toString().equals("SINGLE");
							if (!isSingle) {
								return getContainerSize(world, pos) * 2;
							}
						}
						return getContainerSize(world, pos);
					}
				}.getAmount(world, new BlockPos((int) (x + 1), (int) y, (int) z)) > 0) {
					if (entity instanceof ItemEntity) {
						int insertCount = ((entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).getCount());
						ItemStack stackToInsert = (entity instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).copy();
						stackToInsert.setCount(1);
						BlockPos pos = new BlockPos((int) ((x + 1)), (int) (y), (int) (z));
						BlockEntity Bentity = world.getBlockEntity(pos);
						if (Bentity instanceof BaseContainerBlockEntity blockInv) {
							int size = blockInv.getContainerSize();
							for (int slot = 0; slot < size && insertCount > 0; slot++) {
								ItemStack slotStack = blockInv.getItem(slot);
								if (slotStack.isEmpty()) {
									int amount = Math.min(insertCount, stackToInsert.getMaxStackSize());
									ItemStack toInsert = stackToInsert.copy();
									toInsert.setCount(amount);
									blockInv.setItem(slot, toInsert);
									insertCount -= amount;
									continue;
								}
								if (ItemStack.isSameItemSameTags(slotStack, stackToInsert) && slotStack.getCount() < slotStack.getMaxStackSize()) {
									int room = slotStack.getMaxStackSize() - slotStack.getCount();
									int amountToAdd = Math.min(room, insertCount);
									slotStack.grow(amountToAdd);
									insertCount -= amountToAdd;
								}
							}
							blockInv.setChanged();
							if (world instanceof net.minecraft.world.level.Level _level)
								_level.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
						}
						if (!entity.level().isClientSide())
							entity.discard();
					}
				} else {
					entity.push((power * 1), 0, 0);
				}
			}
		}
	}
}