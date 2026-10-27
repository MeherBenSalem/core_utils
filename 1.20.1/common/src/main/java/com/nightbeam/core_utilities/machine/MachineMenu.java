package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.item.UpgradeItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** One menu class for every machine; the {@link MachineSpec} decides the slot layout. */
public class MachineMenu extends AbstractContainerMenu {
	private final MachineSpec spec;
	private final Container container;
	private final ContainerData data;
	@Nullable
	private final MachineBlockEntity machine;

	/** Client side constructor. */
	public MachineMenu(MachineSpec spec, int id, Inventory inventory) {
		this(spec, id, inventory, null, new SimpleContainerData(MachineBlockEntity.DATA_SIZE));
	}

	public MachineMenu(MachineSpec spec, int id, Inventory inventory, @Nullable MachineBlockEntity machine, ContainerData data) {
		super(spec.menuType(), id);
		this.spec = spec;
		this.machine = machine;
		this.container = machine != null ? machine : new SimpleContainer(spec.size());
		this.data = data;
		checkContainerSize(container, spec.size());
		checkContainerDataCount(data, MachineBlockEntity.DATA_SIZE);
		for (int i = 0; i < spec.size(); i++) {
			MachineSpec.SlotDef def = spec.slots().get(i);
			int offset = def.large() ? 5 : 1;
			addSlot(new MachineSlot(container, i, def.x() + offset, def.y() + offset, def.kind()));
		}
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
			}
		}
		for (int column = 0; column < 9; column++) {
			addSlot(new Slot(inventory, column, 8 + column * 18, 142));
		}
		addDataSlots(data);
	}

	public MachineSpec getSpec() {
		return spec;
	}

	public int getData(int index) {
		return data.get(index);
	}

	public int getEnergy() {
		return (data.get(MachineBlockEntity.DATA_ENERGY_LO) & 0xFFFF) | ((data.get(MachineBlockEntity.DATA_ENERGY_HI) & 0xFFFF) << 16);
	}

	public int getCapacity() {
		return (data.get(MachineBlockEntity.DATA_CAPACITY_LO) & 0xFFFF) | ((data.get(MachineBlockEntity.DATA_CAPACITY_HI) & 0xFFFF) << 16);
	}

	public SideMode getSide(RelativeSide side) {
		return SideMode.byId(data.get(MachineBlockEntity.DATA_SIDES + side.ordinal()));
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		return machine != null && machine.handleButton(player, id);
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getItem();
		ItemStack copy = stack.copy();
		int machineSlots = spec.size();
		int total = slots.size();
		if (index < machineSlots) {
			if (!moveItemStackTo(stack, machineSlots, total, true)) {
				return ItemStack.EMPTY;
			}
		} else {
			boolean moved = false;
			if (stack.getItem() instanceof UpgradeItem) {
				moved = moveToKind(stack, SlotKind.UPGRADE);
			}
			if (!moved) {
				moved = moveToKind(stack, SlotKind.FUEL) || moveToKind(stack, SlotKind.INPUT);
			}
			if (!moved) {
				int hotbarStart = total - 9;
				if (index < hotbarStart) {
					if (!moveItemStackTo(stack, hotbarStart, total, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!moveItemStackTo(stack, machineSlots, hotbarStart, false)) {
					return ItemStack.EMPTY;
				}
			}
		}
		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		if (stack.getCount() == copy.getCount()) {
			return ItemStack.EMPTY;
		}
		slot.onTake(player, stack);
		return copy;
	}

	private boolean moveToKind(ItemStack stack, SlotKind kind) {
		boolean moved = false;
		for (int i : spec.indices(kind)) {
			if (stack.isEmpty()) {
				break;
			}
			if (slots.get(i).mayPlace(stack) && moveItemStackTo(stack, i, i + 1, false)) {
				moved = true;
			}
		}
		return moved;
	}

	private static class MachineSlot extends Slot {
		private final SlotKind kind;

		MachineSlot(Container container, int index, int x, int y, SlotKind kind) {
			super(container, index, x, y);
			this.kind = kind;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			if (kind == SlotKind.OUTPUT || (kind == SlotKind.UPGRADE && !(stack.getItem() instanceof UpgradeItem))) {
				return false;
			}
			return container.canPlaceItem(getContainerSlot(), stack);
		}

		@Override
		public int getMaxStackSize(ItemStack stack) {
			return kind == SlotKind.UPGRADE ? 8 : super.getMaxStackSize(stack);
		}
	}
}
