package com.nightbeam.core_utilities.conduit;

import com.nightbeam.core_utilities.platform.Services;
import com.nightbeam.core_utilities.transfer.ItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Pipez style instant item transport: faces set to "extract" pull a stack from the attached inventory every few
 * ticks and hand it to the next inventory on the network that accepts it (round robin, nearest first).
 */
public class ItemPipeBlockEntity extends ConduitBlockEntity {
	public ItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void serverTick() {
		if (level == null || !(getBlockState().getBlock() instanceof ItemPipeBlock pipe)) {
			return;
		}
		if ((level.getGameTime() + worldPosition.asLong()) % pipe.interval() != 0) {
			return;
		}
		BlockState state = getBlockState();
		for (Direction direction : Direction.values()) {
			if (state.getValue(pipe.property(direction)) == ConnectionType.EXTRACT) {
				extractFrom(direction, pipe.itemsPerOperation());
			}
		}
	}

	private void extractFrom(Direction direction, int maxItems) {
		BlockPos sourcePos = worldPosition.relative(direction);
		ItemAccess source = Services.ITEMS.find(level, sourcePos, direction.getOpposite());
		if (source == null) {
			return;
		}
		ConduitNetwork network = getNetwork();
		List<ConduitNetwork.Endpoint> endpoints = network.endpoints();
		if (endpoints.isEmpty()) {
			return;
		}
		for (int slot = 0; slot < source.getSlots(); slot++) {
			ItemStack available = source.extractItem(slot, maxItems, true);
			if (available.isEmpty()) {
				continue;
			}
			int start = network.nextRoundRobin(endpoints.size());
			for (int i = 0; i < endpoints.size(); i++) {
				ConduitNetwork.Endpoint endpoint = endpoints.get((start + i) % endpoints.size());
				if (endpoint.extract() || endpoint.target().equals(sourcePos) || !level.isLoaded(endpoint.target())) {
					continue;
				}
				ItemAccess target = Services.ITEMS.find(level, endpoint.target(), endpoint.targetFace());
				if (target == null) {
					continue;
				}
				ItemStack rest = target.insert(available, true);
				int accepted = available.getCount() - rest.getCount();
				if (accepted <= 0) {
					continue;
				}
				ItemStack moved = source.extractItem(slot, accepted, false);
				ItemStack leftover = target.insert(moved, false);
				if (!leftover.isEmpty()) {
					ItemStack back = source.insertItem(slot, leftover, false);
					if (!back.isEmpty()) {
						net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, back);
					}
				}
				return;
			}
		}
	}
}
