package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import com.nightbeam.core_utilities.energy.EnergyBuffer;
import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Solar generation: full rate on clear days, half in rain, a quarter in thunderstorms, nothing at night or without
 * sky access. Pushes energy into any neighbour below or beside it.
 */
public class SolarPanelBlockEntity extends BlockEntity implements EnergyHandlerProvider {
	private final int tier;
	private final EnergyBuffer energy;
	private final EnergyHandler handler;
	private int lastGeneration;

	public SolarPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
		super(type, pos, state);
		this.tier = tier;
		this.energy = new EnergyBuffer(rate(tier) * 400, 0, rate(tier) * 4);
		this.handler = EnergyHandler.of(energy, this::setChanged, false, true);
	}

	public static int rate(int tier) {
		CoreUtilitiesConfig c = CoreUtilitiesConfig.get();
		return switch (tier) {
			case 2 -> c.solarTier2Rate;
			case 3 -> c.solarTier3Rate;
			case 4 -> c.solarTier4Rate;
			default -> c.solarTier1Rate;
		};
	}

	public int getLastGeneration() {
		return lastGeneration;
	}

	public EnergyBuffer getEnergy() {
		return energy;
	}

	/** 0 = clear, 1 = rain, 2 = thunder, matching the panel's weather textures. */
	public int weather() {
		if (level.isThundering()) {
			return 2;
		}
		return level.isRaining() ? 1 : 0;
	}

	public void serverTick() {
		boolean sun = level.isDay() && level.canSeeSky(worldPosition.above());
		int weather = weather();
		lastGeneration = 0;
		if (sun) {
			int base = rate(tier);
			lastGeneration = energy.generate(switch (weather) {
				case 2 -> base / 4;
				case 1 -> base / 2;
				default -> base;
			});
		}
		if (level.getGameTime() % 20 == 0) {
			BlockState state = getBlockState();
			if (state.hasProperty(SolarPanelBlock.WEATHER) && state.getValue(SolarPanelBlock.WEATHER) != weather) {
				level.setBlock(worldPosition, state.setValue(SolarPanelBlock.WEATHER, weather), 3);
			}
		}
		if (energy.getEnergyStored() > 0) {
			for (Direction direction : Direction.values()) {
				if (direction == Direction.UP || energy.getEnergyStored() <= 0) {
					continue;
				}
				EnergyHandler target = Services.ENERGY.find(level, worldPosition.relative(direction), direction.getOpposite());
				if (target != null && target.canReceive()) {
					int accepted = target.receiveEnergy(Math.min(energy.getEnergyStored(), rate(tier) * 4), false);
					energy.consume(accepted);
				}
			}
		}
		setChanged();
	}

	@Override
	@Nullable
	public EnergyHandler getEnergyHandler(@Nullable Direction side) {
		return side == Direction.UP ? null : handler;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		energy.setEnergyStored(tag.getInt("Energy"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("Energy", energy.getEnergyStored());
	}
}
