package com.nightbeam.core_utilities.machine;

public enum RedstoneMode {
	IGNORED,
	HIGH,
	LOW;

	public RedstoneMode next() {
		return values()[(ordinal() + 1) % values().length];
	}

	public boolean canRun(boolean powered) {
		return switch (this) {
			case IGNORED -> true;
			case HIGH -> powered;
			case LOW -> !powered;
		};
	}

	public static RedstoneMode byId(int id) {
		return id >= 0 && id < values().length ? values()[id] : IGNORED;
	}
}
