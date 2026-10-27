package com.nightbeam.core_utilities.conduit;

import net.minecraft.util.StringRepresentable;

public enum ConnectionType implements StringRepresentable {
	NONE("none"),
	CONDUIT("conduit"),
	ENDPOINT("endpoint"),
	EXTRACT("extract");

	private final String name;

	ConnectionType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public boolean isConnected() {
		return this != NONE;
	}

	public boolean isEndpoint() {
		return this == ENDPOINT || this == EXTRACT;
	}
}
