package com.nightbeam.core_utilities.conduit;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;

public enum CableTier {
	BASIC("basic"),
	ADVANCED("advanced"),
	ELITE("elite");

	private final String name;

	CableTier(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/** FE per tick a single network of this tier can move. */
	public int transferRate() {
		return switch (this) {
			case BASIC -> CoreUtilitiesConfig.get().cableBasicRate;
			case ADVANCED -> CoreUtilitiesConfig.get().cableAdvancedRate;
			case ELITE -> CoreUtilitiesConfig.get().cableEliteRate;
		};
	}
}
