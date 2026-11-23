package com.nightbeam.core_utilities.energy;

/**
 * Loader independent view of something that stores Forge Energy compatible units (FE).
 * Platform adapters expose this to NeoForge/Forge capabilities and Team Reborn Energy on Fabric.
 */
public interface EnergyHandler {
	int receiveEnergy(int amount, boolean simulate);

	int extractEnergy(int amount, boolean simulate);

	int getEnergyStored();

	int getMaxEnergyStored();

	boolean canReceive();

	boolean canExtract();

	static EnergyHandler of(EnergyBuffer buffer, Runnable onChange, boolean receive, boolean extract) {
		return new EnergyHandler() {
			@Override
			public int receiveEnergy(int amount, boolean simulate) {
				if (!receive) {
					return 0;
				}
				int received = buffer.receiveEnergy(amount, simulate);
				if (!simulate && received > 0) {
					onChange.run();
				}
				return received;
			}

			@Override
			public int extractEnergy(int amount, boolean simulate) {
				if (!extract) {
					return 0;
				}
				int extracted = buffer.extractEnergy(amount, simulate);
				if (!simulate && extracted > 0) {
					onChange.run();
				}
				return extracted;
			}

			@Override
			public int getEnergyStored() {
				return buffer.getEnergyStored();
			}

			@Override
			public int getMaxEnergyStored() {
				return buffer.getMaxEnergyStored();
			}

			@Override
			public boolean canReceive() {
				return receive && buffer.canReceive();
			}

			@Override
			public boolean canExtract() {
				return extract && buffer.canExtract();
			}
		};
	}
}
