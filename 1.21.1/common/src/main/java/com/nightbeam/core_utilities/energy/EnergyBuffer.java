package com.nightbeam.core_utilities.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class EnergyBuffer {
	private int energy;
	private final int capacity;
	private final int maxReceive;
	private final int maxExtract;

	public EnergyBuffer(int capacity, int maxReceive, int maxExtract) {
		this(capacity, maxReceive, maxExtract, 0);
	}

	public EnergyBuffer(int capacity, int maxReceive, int maxExtract, int initialEnergy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.min(initialEnergy, capacity);
	}

	public int getEnergyStored() {
		return energy;
	}

	public int getMaxEnergyStored() {
		return capacity;
	}

	public int getMaxReceive() {
		return maxReceive;
	}

	public int getMaxExtract() {
		return maxExtract;
	}

	public boolean canExtract() {
		return maxExtract > 0;
	}

	public boolean canReceive() {
		return maxReceive > 0;
	}

	public int receiveEnergy(int amount, boolean simulate) {
		if (!canReceive() || amount <= 0) {
			return 0;
		}
		int received = Math.min(capacity - energy, Math.min(maxReceive, amount));
		if (!simulate) {
			energy += received;
		}
		return received;
	}

	public int extractEnergy(int amount, boolean simulate) {
		if (!canExtract() || amount <= 0) {
			return 0;
		}
		int extracted = Math.min(energy, Math.min(maxExtract, amount));
		if (!simulate) {
			energy -= extracted;
		}
		return extracted;
	}

	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("energy", energy);
		return tag;
	}

	public void deserializeNBT(Tag tag) {
		if (tag instanceof IntTag intTag) {
			energy = Math.min(intTag.getAsInt(), capacity);
		} else if (tag instanceof CompoundTag compoundTag) {
			energy = Math.min(compoundTag.getInt("energy"), capacity);
		}
	}
}
