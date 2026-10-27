package com.nightbeam.core_utilities.conduit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class ConduitBlockEntity extends BlockEntity {
	@Nullable
	private ConduitNetwork network;
	/** Faces the player disconnected with the wrench. */
	private int disabledFaces;

	protected ConduitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public ConduitNetwork getNetwork() {
		if (network == null || !network.isValid()) {
			network = ConduitNetwork.build(level, worldPosition, (ConduitBlock) getBlockState().getBlock());
		}
		return network;
	}

	void setNetwork(ConduitNetwork network) {
		this.network = network;
	}

	public void invalidateNetwork() {
		if (network != null) {
			network.invalidate();
			network = null;
		}
	}

	public boolean isFaceDisabled(Direction direction) {
		return (disabledFaces & (1 << direction.get3DDataValue())) != 0;
	}

	public void setFaceDisabled(Direction direction, boolean disabled) {
		if (disabled) {
			disabledFaces |= 1 << direction.get3DDataValue();
		} else {
			disabledFaces &= ~(1 << direction.get3DDataValue());
		}
		setChanged();
	}

	@Override
	public void setRemoved() {
		invalidateNetwork();
		super.setRemoved();
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		disabledFaces = tag.getInt("DisabledFaces");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("DisabledFaces", disabledFaces);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}
}
