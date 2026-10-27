package com.nightbeam.core_utilities.machine;

public enum SideMode {
	NONE,
	INPUT,
	OUTPUT,
	BOTH;

	public SideMode next() {
		return values()[(ordinal() + 1) % values().length];
	}

	public boolean allowsInput() {
		return this == INPUT || this == BOTH;
	}

	public boolean allowsOutput() {
		return this == OUTPUT || this == BOTH;
	}

	public static SideMode byId(int id) {
		return id >= 0 && id < values().length ? values()[id] : NONE;
	}
}
