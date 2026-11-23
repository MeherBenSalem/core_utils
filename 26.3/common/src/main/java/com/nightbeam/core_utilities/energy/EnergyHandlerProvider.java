package com.nightbeam.core_utilities.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/** Implemented by Core Utilities block entities that expose energy on (some of) their faces. */
public interface EnergyHandlerProvider {
	@Nullable
	EnergyHandler getEnergyHandler(@Nullable Direction side);
}
