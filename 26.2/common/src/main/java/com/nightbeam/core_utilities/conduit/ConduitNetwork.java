package com.nightbeam.core_utilities.conduit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A connected group of conduits of one block type. Built lazily by BFS and shared by every member, so a
 * change anywhere in the group invalidates it for all members in O(1).
 */
public class ConduitNetwork {
	public record Endpoint(BlockPos conduit, Direction direction, boolean extract) {
		public BlockPos target() {
			return conduit.relative(direction);
		}

		public Direction targetFace() {
			return direction.getOpposite();
		}
	}

	private final List<BlockPos> members;
	private final List<Endpoint> endpoints;
	private boolean valid = true;
	private long tick = -1;
	private int transferredThisTick;
	private int roundRobin;
	/** Free slot for per-network caches owned by the conduit type. */
	public Object cache;

	private ConduitNetwork(List<BlockPos> members, List<Endpoint> endpoints) {
		this.members = members;
		this.endpoints = endpoints;
	}

	public List<BlockPos> members() {
		return members;
	}

	public List<Endpoint> endpoints() {
		return endpoints;
	}

	public boolean isValid() {
		return valid;
	}

	public void invalidate() {
		valid = false;
	}

	/** Remaining transfer budget for this game tick. */
	public int budget(long gameTime, int perTick) {
		if (gameTime != tick) {
			tick = gameTime;
			transferredThisTick = 0;
		}
		return Math.max(0, perTick - transferredThisTick);
	}

	public void consumeBudget(int amount) {
		transferredThisTick += amount;
	}

	public int nextRoundRobin(int size) {
		if (size <= 0) {
			return 0;
		}
		roundRobin = (roundRobin + 1) % size;
		return roundRobin;
	}

	public static ConduitNetwork build(Level level, BlockPos start, ConduitBlock block) {
		List<BlockPos> members = new ArrayList<>();
		List<Endpoint> endpoints = new ArrayList<>();
		Set<BlockPos> seen = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		queue.add(start);
		seen.add(start);
		int limit = 4096;
		while (!queue.isEmpty() && members.size() < limit) {
			BlockPos pos = queue.poll();
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() != block) {
				continue;
			}
			members.add(pos);
			for (Direction direction : Direction.values()) {
				ConnectionType type = state.getValue(block.property(direction));
				if (type == ConnectionType.CONDUIT) {
					BlockPos next = pos.relative(direction);
					if (seen.add(next) && level.isLoaded(next)) {
						queue.add(next);
					}
				} else if (type.isEndpoint()) {
					endpoints.add(new Endpoint(pos, direction, type == ConnectionType.EXTRACT));
				}
			}
		}
		ConduitNetwork network = new ConduitNetwork(members, endpoints);
		for (BlockPos member : members) {
			BlockEntity entity = level.getBlockEntity(member);
			if (entity instanceof ConduitBlockEntity conduit) {
				conduit.setNetwork(network);
			}
		}
		return network;
	}
}
