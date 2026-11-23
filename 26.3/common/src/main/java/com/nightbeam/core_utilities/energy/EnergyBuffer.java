package com.nightbeam.core_utilities.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

	public void setEnergyStored(int energy) {
		this.energy = Math.max(0, Math.min(energy, capacity));
	}

	public int getFreeSpace() {
		return capacity - energy;
	}

	/** Adds energy ignoring the per-tick receive limit (used by generators). */
	public int generate(int amount) {
		int added = Math.max(0, Math.min(capacity - energy, amount));
		energy += added;
		return added;
	}

	/** Removes energy ignoring the per-tick extract limit (used by machines). */
	public boolean consume(int amount) {
		if (amount > energy) {
			return false;
		}
		energy -= amount;
		return true;
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
			energy = Math.min(intTag.intValue(), capacity);
		} else if (tag instanceof CompoundTag compoundTag) {
			energy = Math.min(compoundTag.getInt("energy").orElse(0), capacity);
		}
	}

	public void save(ValueOutput output) {
		output.putInt("energy", energy);
	}

	public void load(ValueInput input) {
		energy = Math.min(input.getIntOr("energy", 0), capacity);
	}
}
