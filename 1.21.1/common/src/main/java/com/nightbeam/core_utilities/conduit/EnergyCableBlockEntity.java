package com.nightbeam.core_utilities.conduit;

import com.nightbeam.core_utilities.energy.EnergyDistribution;
import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Cables hold no energy. Energy pushed into any cable is routed instantly to the storages connected to the
 * network, split fairly, limited by the tier's transfer rate per tick. No per-cable ticking.
 */
public class EnergyCableBlockEntity extends ConduitBlockEntity implements EnergyHandlerProvider {
	private final Map<Direction, EnergyHandler> handlers = new EnumMap<>(Direction.class);

	public EnergyCableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	private CableTier tier() {
		return getBlockState().getBlock() instanceof EnergyCableBlock cable ? cable.getTier() : CableTier.BASIC;
	}

	@Override
	@Nullable
	public EnergyHandler getEnergyHandler(@Nullable Direction side) {
		if (side == null) {
			return handlers.computeIfAbsent(Direction.UP, d -> new CableHandler(null));
		}
		return handlers.computeIfAbsent(side, CableHandler::new);
	}

	/** Pushes {@code amount} into the network, as if it came from the block at {@code source}. */
	public int distribute(int amount, @Nullable BlockPos source, boolean simulate) {
		if (level == null || level.isClientSide || amount <= 0) {
			return 0;
		}
		ConduitNetwork network = getNetwork();
		int budget = network.budget(level.getGameTime(), tier().transferRate());
		amount = Math.min(amount, budget);
		if (amount <= 0) {
			return 0;
		}
		List<EnergyHandler> receivers = new ArrayList<>();
		for (ConduitNetwork.Endpoint endpoint : network.endpoints()) {
			if (endpoint.target().equals(source) || !level.isLoaded(endpoint.target())) {
				continue;
			}
			EnergyHandler handler = Services.ENERGY.find(level, endpoint.target(), endpoint.targetFace());
			if (handler != null && handler.canReceive()) {
				receivers.add(handler);
			}
		}
		if (receivers.isEmpty()) {
			return 0;
		}
		int[] demands = new int[receivers.size()];
		for (int i = 0; i < demands.length; i++) {
			demands[i] = receivers.get(i).receiveEnergy(amount, true);
		}
		int[] split = EnergyDistribution.split(amount, demands);
		int moved = 0;
		for (int i = 0; i < split.length; i++) {
			if (split[i] > 0) {
				moved += simulate ? split[i] : receivers.get(i).receiveEnergy(split[i], false);
			}
		}
		if (!simulate) {
			network.consumeBudget(moved);
		}
		return moved;
	}

	private class CableHandler implements EnergyHandler {
		@Nullable
		private final Direction side;

		CableHandler(@Nullable Direction side) {
			this.side = side;
		}

		@Override
		public int receiveEnergy(int amount, boolean simulate) {
			return distribute(amount, side == null ? null : worldPosition.relative(side), simulate);
		}

		@Override
		public int extractEnergy(int amount, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return 0;
		}

		@Override
		public int getMaxEnergyStored() {
			return tier().transferRate();
		}

		@Override
		public boolean canReceive() {
			return true;
		}

		@Override
		public boolean canExtract() {
			return false;
		}
	}
}
