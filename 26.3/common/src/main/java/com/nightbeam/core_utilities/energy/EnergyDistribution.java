package com.nightbeam.core_utilities.energy;

/** Pure helper that splits an amount of energy fairly between receivers with limited demand. */
public final class EnergyDistribution {
	private EnergyDistribution() {
	}

	/**
	 * Water-fill split: every receiver gets an equal share, receivers that need less than their share
	 * are filled completely and the remainder is shared between the others.
	 *
	 * @param amount  energy available
	 * @param demands how much each receiver can accept (simulated)
	 * @return how much to send to each receiver; the sum never exceeds {@code amount}
	 */
	public static int[] split(int amount, int[] demands) {
		int n = demands.length;
		int[] result = new int[n];
		if (amount <= 0 || n == 0) {
			return result;
		}
		boolean[] done = new boolean[n];
		int remaining = amount;
		int open = 0;
		for (int i = 0; i < n; i++) {
			if (demands[i] <= 0) {
				done[i] = true;
			} else {
				open++;
			}
		}
		while (remaining > 0 && open > 0) {
			int share = Math.max(1, remaining / open);
			boolean progressed = false;
			for (int i = 0; i < n && remaining > 0; i++) {
				if (done[i]) {
					continue;
				}
				int give = Math.min(Math.min(share, demands[i] - result[i]), remaining);
				if (give > 0) {
					result[i] += give;
					remaining -= give;
					progressed = true;
				}
				if (result[i] >= demands[i]) {
					done[i] = true;
					open--;
				}
			}
			if (!progressed) {
				break;
			}
		}
		return result;
	}
}
