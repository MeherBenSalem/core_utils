package com.nightbeam.core_utilities.block.entity;

import com.nightbeam.core_utilities.energy.EnergyBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public abstract class EnergyBlockEntityBase extends RandomizableContainerBlockEntity implements WorldlyContainer {
	private NonNullList<ItemStack> stacks = NonNullList.withSize(0, ItemStack.EMPTY);
	protected final EnergyBuffer energyBuffer;

	protected EnergyBlockEntityBase(BlockEntityType<?> type, BlockPos position, BlockState state, int capacity, int maxReceive, int maxExtract) {
		super(type, position, state);
		this.energyBuffer = new EnergyBuffer(capacity, maxReceive, maxExtract);
	}

	public EnergyBuffer getEnergyBuffer() {
		return energyBuffer;
	}

	protected abstract Component getEnergyBlockName();

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, this.stacks);
		ValueInput energy = input.childOrEmpty("energyStorage");
		energyBuffer.load(energy);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.stacks);
		energyBuffer.save(output.child("energyStorage"));
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveWithFullMetadata(registries);
	}

	@Override
	public int getContainerSize() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Component getDefaultName() {
		return getEnergyBlockName();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return ChestMenu.threeRows(id, inventory);
	}

	@Override
	public Component getDisplayName() {
		return getEnergyBlockName();
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemstack, @Nullable Direction direction) {
		return this.canPlaceItem(index, itemstack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack itemstack, Direction direction) {
		return true;
	}

	protected void markEnergyChanged() {
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
		}
	}

	public int receiveEnergy(int maxReceive, boolean simulate) {
		int received = energyBuffer.receiveEnergy(maxReceive, simulate);
		if (!simulate && received > 0) {
			markEnergyChanged();
		}
		return received;
	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		int extracted = energyBuffer.extractEnergy(maxExtract, simulate);
		if (!simulate && extracted > 0) {
			markEnergyChanged();
		}
		return extracted;
	}
}
