package com.nightbeam.core_utilities.energy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnergyBufferTest {
	@Test
	void receiveRespectsRateAndCapacity() {
		EnergyBuffer buffer = new EnergyBuffer(1000, 100, 50);
		assertEquals(100, buffer.receiveEnergy(500, false));
		assertEquals(100, buffer.getEnergyStored());
		buffer.setEnergyStored(950);
		assertEquals(50, buffer.receiveEnergy(100, true));
		assertEquals(950, buffer.getEnergyStored(), "simulate must not change the buffer");
	}

	@Test
	void extractRespectsRate() {
		EnergyBuffer buffer = new EnergyBuffer(1000, 100, 50, 500);
		assertEquals(50, buffer.extractEnergy(200, false));
		assertEquals(450, buffer.getEnergyStored());
	}

	@Test
	void generateAndConsumeIgnoreRates() {
		EnergyBuffer buffer = new EnergyBuffer(1000, 0, 0);
		assertFalse(buffer.canReceive());
		assertEquals(0, buffer.receiveEnergy(10, false));
		assertEquals(1000, buffer.generate(5000));
		assertTrue(buffer.consume(600));
		assertFalse(buffer.consume(600), "cannot consume more than stored");
		assertEquals(400, buffer.getEnergyStored());
	}

	@Test
	void setEnergyIsClamped() {
		EnergyBuffer buffer = new EnergyBuffer(100, 10, 10);
		buffer.setEnergyStored(-5);
		assertEquals(0, buffer.getEnergyStored());
		buffer.setEnergyStored(500);
		assertEquals(100, buffer.getEnergyStored());
	}
}
