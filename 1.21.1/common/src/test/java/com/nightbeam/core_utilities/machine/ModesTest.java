package com.nightbeam.core_utilities.machine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModesTest {
	@Test
	void sideModesCycleThroughEveryValue() {
		SideMode mode = SideMode.NONE;
		for (int i = 0; i < SideMode.values().length; i++) {
			mode = mode.next();
		}
		assertEquals(SideMode.NONE, mode);
		assertTrue(SideMode.BOTH.allowsInput() && SideMode.BOTH.allowsOutput());
		assertFalse(SideMode.INPUT.allowsOutput());
		assertEquals(SideMode.NONE, SideMode.byId(42));
	}

	@Test
	void redstoneModes() {
		assertTrue(RedstoneMode.IGNORED.canRun(true) && RedstoneMode.IGNORED.canRun(false));
		assertTrue(RedstoneMode.HIGH.canRun(true));
		assertFalse(RedstoneMode.HIGH.canRun(false));
		assertTrue(RedstoneMode.LOW.canRun(false));
		assertFalse(RedstoneMode.LOW.canRun(true));
	}
}
