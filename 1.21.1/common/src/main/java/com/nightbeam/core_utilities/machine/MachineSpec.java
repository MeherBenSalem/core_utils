package com.nightbeam.core_utilities.machine;

import net.minecraft.world.inventory.MenuType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Describes a machine's inventory and GUI layout. Shared by the block entity, the menu, the screen and the
 * recipe viewer plugin so they can never disagree.
 */
public class MachineSpec {
	public record SlotDef(SlotKind kind, int x, int y, boolean large) {
	}

	public enum Progress {
		NONE,
		ARROW,
		FLAME
	}

	private final String name;
	private final List<SlotDef> slots = new ArrayList<>();
	private Progress progress = Progress.NONE;
	private int progressX = 79;
	private int progressY = 34;
	private boolean upgrades = true;
	private boolean sideConfig = true;
	private boolean energyBar = true;
	private Supplier<MenuType<MachineMenu>> menuType;

	public MachineSpec(String name) {
		this.name = name;
	}

	public MachineSpec slot(SlotKind kind, int x, int y) {
		slots.add(new SlotDef(kind, x, y, false));
		return this;
	}

	public MachineSpec bigSlot(SlotKind kind, int x, int y) {
		slots.add(new SlotDef(kind, x, y, true));
		return this;
	}

	/** Adds a grid of {@code columns x rows} slots starting at x/y. */
	public MachineSpec grid(SlotKind kind, int x, int y, int columns, int rows) {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				slot(kind, x + column * 18, y + row * 18);
			}
		}
		return this;
	}

	public MachineSpec progress(Progress progress, int x, int y) {
		this.progress = progress;
		this.progressX = x;
		this.progressY = y;
		return this;
	}

	public MachineSpec noUpgrades() {
		this.upgrades = false;
		return this;
	}

	public MachineSpec noSideConfig() {
		this.sideConfig = false;
		return this;
	}

	public MachineSpec noEnergyBar() {
		this.energyBar = false;
		return this;
	}

	/** Upgrade slots are always the last three slots. */
	public MachineSpec finish() {
		if (upgrades) {
			slot(SlotKind.UPGRADE, 152, 17);
			slot(SlotKind.UPGRADE, 152, 35);
			slot(SlotKind.UPGRADE, 152, 53);
		}
		return this;
	}

	public void setMenuType(Supplier<MenuType<MachineMenu>> menuType) {
		this.menuType = menuType;
	}

	public MenuType<MachineMenu> menuType() {
		return menuType.get();
	}

	public String name() {
		return name;
	}

	public List<SlotDef> slots() {
		return slots;
	}

	public int size() {
		return slots.size();
	}

	public SlotKind kind(int slot) {
		return slots.get(slot).kind();
	}

	public int[] indices(SlotKind kind) {
		return java.util.stream.IntStream.range(0, slots.size()).filter(i -> slots.get(i).kind() == kind).toArray();
	}

	public Progress progress() {
		return progress;
	}

	public int progressX() {
		return progressX;
	}

	public int progressY() {
		return progressY;
	}

	public boolean hasUpgrades() {
		return upgrades;
	}

	public boolean hasSideConfig() {
		return sideConfig;
	}

	public boolean hasEnergyBar() {
		return energyBar;
	}
}
