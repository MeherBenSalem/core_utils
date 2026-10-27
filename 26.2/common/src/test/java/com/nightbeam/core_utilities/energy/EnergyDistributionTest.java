package com.nightbeam.core_utilities.energy;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnergyDistributionTest {
	@Test
	void splitsEvenlyWhenEveryoneWantsMore() {
		assertArrayEquals(new int[] {100, 100, 100}, EnergyDistribution.split(300, new int[] {1000, 1000, 1000}));
	}

	@Test
	void smallDemandsAreFilledAndTheRestIsShared() {
		int[] result = EnergyDistribution.split(300, new int[] {10, 1000, 1000});
		assertEquals(10, result[0]);
		assertEquals(145, result[1]);
		assertEquals(145, result[2]);
	}

	@Test
	void neverGivesMoreThanAvailableOrDemanded() {
		int[] demands = {7, 0, 3, 50, 1};
		for (int amount = 0; amount < 100; amount++) {
			int[] result = EnergyDistribution.split(amount, demands);
			assertTrue(Arrays.stream(result).sum() <= amount);
			for (int i = 0; i < demands.length; i++) {
				assertTrue(result[i] <= demands[i] && result[i] >= 0);
			}
			assertEquals(Math.min(amount, Arrays.stream(demands).sum()), Arrays.stream(result).sum(), "everything that fits is sent");
		}
	}

	@Test
	void unevenAmountsLoseNothing() {
		int[] result = EnergyDistribution.split(10, new int[] {100, 100, 100});
		assertEquals(10, Arrays.stream(result).sum());
	}

	@Test
	void emptyInputs() {
		assertArrayEquals(new int[0], EnergyDistribution.split(100, new int[0]));
		assertArrayEquals(new int[] {0, 0}, EnergyDistribution.split(0, new int[] {5, 5}));
	}
}
