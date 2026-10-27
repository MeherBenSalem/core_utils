package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;

public enum CellTier {
	BASIC,
	ADVANCED,
	ELITE,
	CREATIVE;

	public int capacity() {
		CoreUtilitiesConfig c = CoreUtilitiesConfig.get();
		return switch (this) {
			case BASIC -> c.cellBasicCapacity;
			case ADVANCED -> c.cellAdvancedCapacity;
			case ELITE -> c.cellEliteCapacity;
			case CREATIVE -> Integer.MAX_VALUE;
		};
	}

	public int rate() {
		CoreUtilitiesConfig c = CoreUtilitiesConfig.get();
		return switch (this) {
			case BASIC -> c.cellBasicRate;
			case ADVANCED -> c.cellAdvancedRate;
			case ELITE -> c.cellEliteRate;
			case CREATIVE -> Integer.MAX_VALUE / 4;
		};
	}
}
