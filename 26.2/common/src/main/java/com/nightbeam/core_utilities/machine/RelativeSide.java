package com.nightbeam.core_utilities.machine;

import net.minecraft.core.Direction;

/** Machine faces relative to the front, so side configuration survives rotating the machine. */
public enum RelativeSide {
	FRONT,
	BACK,
	LEFT,
	RIGHT,
	TOP,
	BOTTOM;

	public Direction toAbsolute(Direction facing) {
		return switch (this) {
			case FRONT -> facing;
			case BACK -> facing.getOpposite();
			case LEFT -> facing.getClockWise();
			case RIGHT -> facing.getCounterClockWise();
			case TOP -> Direction.UP;
			case BOTTOM -> Direction.DOWN;
		};
	}

	public static RelativeSide fromAbsolute(Direction facing, Direction side) {
		if (side == Direction.UP) {
			return TOP;
		}
		if (side == Direction.DOWN) {
			return BOTTOM;
		}
		if (side == facing) {
			return FRONT;
		}
		if (side == facing.getOpposite()) {
			return BACK;
		}
		return side == facing.getClockWise() ? LEFT : RIGHT;
	}
}
