package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.energy.EnergyBuffer;
import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.item.UpgradeItem;
import com.nightbeam.core_utilities.platform.Services;
import com.nightbeam.core_utilities.transfer.ItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * Base for every powered Core Utilities block: FE buffer, inventory with per-face side configuration, redstone
 * control, upgrade slots, auto-eject and GUI data sync. Subclasses implement {@link #work()}.
 */
public abstract class MachineBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, EnergyHandlerProvider {
	public static final int DATA_ENERGY_LO = 0;
	public static final int DATA_ENERGY_HI = 1;
	public static final int DATA_CAPACITY_LO = 2;
	public static final int DATA_CAPACITY_HI = 3;
	public static final int DATA_PROGRESS = 4;
	public static final int DATA_MAX_PROGRESS = 5;
	public static final int DATA_REDSTONE = 6;
	public static final int DATA_AUTO_EJECT = 7;
	public static final int DATA_SIDES = 8;
	public static final int DATA_USAGE = 14;
	public static final int DATA_EXTRA = 15;
	public static final int DATA_SIZE = 16;

	public static final int BUTTON_REDSTONE = 0;
	public static final int BUTTON_AUTO_EJECT = 1;
	public static final int BUTTON_SIDE = 10;

	protected final MachineSpec spec;
	protected NonNullList<ItemStack> items;
	protected final EnergyBuffer energy;
	protected final SideMode[] sides = new SideMode[6];
	protected RedstoneMode redstoneMode = RedstoneMode.IGNORED;
	protected boolean autoEject = true;
	protected int progress;
	protected int maxProgress;
	/** Energy used (or generated) last tick, for the GUI. */
	protected int lastUsage;
	protected int extra;
	private final Map<Direction, EnergyHandler> energyHandlers = new EnumMap<>(Direction.class);
	private boolean active;

	protected final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case DATA_ENERGY_LO -> energy.getEnergyStored() & 0xFFFF;
				case DATA_ENERGY_HI -> (energy.getEnergyStored() >>> 16) & 0xFFFF;
				case DATA_CAPACITY_LO -> energy.getMaxEnergyStored() & 0xFFFF;
				case DATA_CAPACITY_HI -> (energy.getMaxEnergyStored() >>> 16) & 0xFFFF;
				case DATA_PROGRESS -> progress;
				case DATA_MAX_PROGRESS -> maxProgress;
				case DATA_REDSTONE -> redstoneMode.ordinal();
				case DATA_AUTO_EJECT -> autoEject ? 1 : 0;
				case DATA_USAGE -> Math.min(lastUsage, Short.MAX_VALUE);
				case DATA_EXTRA -> extra;
				default -> index >= DATA_SIDES && index < DATA_SIDES + 6 ? sides[index - DATA_SIDES].ordinal() : 0;
			};
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int getCount() {
			return DATA_SIZE;
		}
	};

	protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec, int capacity, int maxReceive, int maxExtract) {
		super(type, pos, state);
		this.spec = spec;
		this.items = NonNullList.withSize(spec.size(), ItemStack.EMPTY);
		this.energy = new EnergyBuffer(capacity, maxReceive, maxExtract);
		Arrays.fill(sides, SideMode.NONE);
		defaultSides(sides);
	}

	/** Sets the default configuration, indexed by {@link RelativeSide}. */
	protected void defaultSides(SideMode[] sides) {
		sides[RelativeSide.TOP.ordinal()] = SideMode.INPUT;
		sides[RelativeSide.LEFT.ordinal()] = SideMode.INPUT;
		sides[RelativeSide.RIGHT.ordinal()] = SideMode.INPUT;
		sides[RelativeSide.BACK.ordinal()] = SideMode.INPUT;
		sides[RelativeSide.BOTTOM.ordinal()] = SideMode.OUTPUT;
	}

	public MachineSpec getSpec() {
		return spec;
	}

	public EnergyBuffer getEnergy() {
		return energy;
	}

	public ContainerData getData() {
		return data;
	}

	/** Called every server tick. */
	public final void serverTick() {
		if (level == null) {
			return;
		}
		lastUsage = 0;
		boolean powered = level.hasNeighborSignal(worldPosition);
		boolean running = redstoneMode.canRun(powered) && work();
		if (!redstoneMode.canRun(powered)) {
			idle();
		}
		setActive(running);
		if (autoEject && level.getGameTime() % 10 == Math.floorMod(worldPosition.hashCode(), 10)) {
			ejectOutputs();
		}
		afterTick();
	}

	/** Performs one tick of work. @return whether the machine is active (drives the lit model). */
	protected abstract boolean work();

	/** Called when redstone control stops the machine. */
	protected void idle() {
	}

	protected void afterTick() {
	}

	protected void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			BlockState state = getBlockState();
			if (state.hasProperty(MachineBlock.ACTIVE) && state.getValue(MachineBlock.ACTIVE) != active) {
				level.setBlock(worldPosition, state.setValue(MachineBlock.ACTIVE, active), 3);
			}
		}
	}

	public Direction getFacing() {
		BlockState state = getBlockState();
		return state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
	}

	public SideMode getSideMode(Direction absolute) {
		return sides[RelativeSide.fromAbsolute(getFacing(), absolute).ordinal()];
	}

	public void cycleSide(RelativeSide side) {
		sides[side.ordinal()] = sides[side.ordinal()].next();
		setChanged();
		refreshExposedFaces();
	}

	/** Lets loaders refresh cached capabilities after a side configuration change. */
	protected void refreshExposedFaces() {
		if (level != null && !level.isClientSide) {
			Services.PLATFORM.invalidateCapabilities(level, worldPosition);
			level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		}
	}

	public boolean handleButton(Player player, int id) {
		if (id == BUTTON_REDSTONE) {
			redstoneMode = redstoneMode.next();
		} else if (id == BUTTON_AUTO_EJECT) {
			autoEject = !autoEject;
		} else if (id >= BUTTON_SIDE && id < BUTTON_SIDE + 6 && spec.hasSideConfig()) {
			cycleSide(RelativeSide.values()[id - BUTTON_SIDE]);
		} else {
			return false;
		}
		setChanged();
		return true;
	}

	// ---- upgrades ----

	public int countUpgrades(UpgradeItem.Type type) {
		int count = 0;
		for (int slot : spec.indices(SlotKind.UPGRADE)) {
			ItemStack stack = items.get(slot);
			if (stack.getItem() instanceof UpgradeItem upgrade && upgrade.getType() == type) {
				count += stack.getCount();
			}
		}
		return Math.min(count, 8);
	}

	/** Ticks per operation after speed upgrades (each one makes the machine 50% faster). */
	protected int scaledTime(int baseTicks) {
		double speed = (1.0 + 0.5 * countUpgrades(UpgradeItem.Type.SPEED)) * com.nightbeam.core_utilities.config.CoreUtilitiesConfig.get().machineSpeedMultiplier;
		return Math.max(1, (int) Math.round(baseTicks / speed));
	}

	/** FE per tick after upgrades: speed costs more power per tick, energy upgrades save 15% each. */
	protected int scaledUsage(int baseUsage) {
		double usage = baseUsage * (1.0 + 0.5 * countUpgrades(UpgradeItem.Type.SPEED)) * Math.pow(0.85, countUpgrades(UpgradeItem.Type.ENERGY));
		return Math.max(1, (int) Math.round(usage * com.nightbeam.core_utilities.config.CoreUtilitiesConfig.get().machineEnergyMultiplier));
	}

	// ---- energy ----

	protected boolean canReceiveEnergyFrom(@Nullable Direction side) {
		return energy.canReceive();
	}

	protected boolean canExtractEnergyFrom(@Nullable Direction side) {
		return energy.canExtract();
	}

	@Override
	@Nullable
	public EnergyHandler getEnergyHandler(@Nullable Direction side) {
		Direction key = side == null ? Direction.UP : side;
		if (side != null && !canReceiveEnergyFrom(side) && !canExtractEnergyFrom(side)) {
			return null;
		}
		return energyHandlers.computeIfAbsent(key, k -> new SidedEnergy(side));
	}

	private class SidedEnergy implements EnergyHandler {
		@Nullable
		private final Direction side;

		SidedEnergy(@Nullable Direction side) {
			this.side = side;
		}

		@Override
		public int receiveEnergy(int amount, boolean simulate) {
			if (!canReceiveEnergyFrom(side)) {
				return 0;
			}
			int received = energy.receiveEnergy(amount, simulate);
			if (received > 0 && !simulate) {
				setChanged();
			}
			return received;
		}

		@Override
		public int extractEnergy(int amount, boolean simulate) {
			if (!canExtractEnergyFrom(side)) {
				return 0;
			}
			int extracted = energy.extractEnergy(amount, simulate);
			if (extracted > 0 && !simulate) {
				setChanged();
			}
			return extracted;
		}

		@Override
		public int getEnergyStored() {
			return energy.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			return energy.getMaxEnergyStored();
		}

		@Override
		public boolean canReceive() {
			return canReceiveEnergyFrom(side);
		}

		@Override
		public boolean canExtract() {
			return canExtractEnergyFrom(side);
		}
	}

	/** Pushes stored energy to neighbours on the given faces. */
	protected void pushEnergy(int maxPerFace, java.util.function.Predicate<Direction> faces) {
		if (energy.getEnergyStored() <= 0) {
			return;
		}
		for (Direction direction : Direction.values()) {
			if (energy.getEnergyStored() <= 0) {
				break;
			}
			if (!faces.test(direction)) {
				continue;
			}
			EnergyHandler target = Services.ENERGY.find(level, worldPosition.relative(direction), direction.getOpposite());
			if (target == null || !target.canReceive()) {
				continue;
			}
			int offer = Math.min(maxPerFace, energy.getEnergyStored());
			int accepted = target.receiveEnergy(offer, false);
			if (accepted > 0) {
				energy.consume(accepted);
				setChanged();
			}
		}
	}

	// ---- items ----

	protected void ejectOutputs() {
		int[] outputs = spec.indices(SlotKind.OUTPUT);
		if (outputs.length == 0) {
			return;
		}
		for (Direction direction : Direction.values()) {
			if (!getSideMode(direction).allowsOutput()) {
				continue;
			}
			ItemAccess target = Services.ITEMS.find(level, worldPosition.relative(direction), direction.getOpposite());
			if (target == null) {
				continue;
			}
			for (int slot : outputs) {
				ItemStack stack = items.get(slot);
				if (stack.isEmpty()) {
					continue;
				}
				ItemStack rest = target.insert(stack.copy(), false);
				if (rest.getCount() != stack.getCount()) {
					items.set(slot, rest);
					setChanged();
				}
			}
		}
	}

	/** Tries to merge {@code result} into the output slots. */
	protected boolean canOutput(ItemStack result, int[] outputs) {
		if (result.isEmpty()) {
			return true;
		}
		int remaining = result.getCount();
		for (int slot : outputs) {
			ItemStack existing = items.get(slot);
			if (existing.isEmpty()) {
				return true;
			}
			if (ItemStack.isSameItemSameComponents(existing, result)) {
				remaining -= existing.getMaxStackSize() - existing.getCount();
				if (remaining <= 0) {
					return true;
				}
			}
		}
		return false;
	}

	protected ItemStack output(ItemStack result, int[] outputs) {
		ItemStack remaining = result.copy();
		for (int slot : outputs) {
			ItemStack existing = items.get(slot);
			if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, remaining)) {
				int moved = Math.min(remaining.getCount(), existing.getMaxStackSize() - existing.getCount());
				existing.grow(moved);
				remaining.shrink(moved);
			}
			if (remaining.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}
		for (int slot : outputs) {
			if (items.get(slot).isEmpty()) {
				items.set(slot, remaining);
				return ItemStack.EMPTY;
			}
		}
		return remaining;
	}

	public boolean isValidInput(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int slot) {
		return items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack result = ContainerHelper.removeItem(items, slot, amount);
		if (!result.isEmpty()) {
			setChanged();
		}
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(items, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		items.set(slot, stack);
		if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return switch (spec.kind(slot)) {
			case OUTPUT -> false;
			case UPGRADE -> stack.getItem() instanceof UpgradeItem upgrade && isUpgradeSupported(upgrade.getType());
			default -> isValidInput(slot, stack);
		};
	}

	public boolean isUpgradeSupported(UpgradeItem.Type type) {
		return type != UpgradeItem.Type.RANGE;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		SideMode mode = getSideMode(side);
		if (!spec.hasSideConfig()) {
			mode = SideMode.BOTH;
		}
		int[] inputs = mode.allowsInput() ? concat(spec.indices(SlotKind.INPUT), spec.indices(SlotKind.FUEL)) : new int[0];
		int[] outputs = mode.allowsOutput() ? spec.indices(SlotKind.OUTPUT) : new int[0];
		return concat(inputs, outputs);
	}

	private static int[] concat(int[] a, int[] b) {
		int[] result = Arrays.copyOf(a, a.length + b.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
		SlotKind kind = spec.kind(slot);
		return (kind == SlotKind.INPUT || kind == SlotKind.FUEL) && canPlaceItem(slot, stack) && (side == null || !spec.hasSideConfig() || getSideMode(side).allowsInput());
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return spec.kind(slot) == SlotKind.OUTPUT && (!spec.hasSideConfig() || getSideMode(side).allowsOutput());
	}

	// ---- menu ----

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.core_utilities." + spec.name());
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new MachineMenu(spec, id, inventory, this, data);
	}

	// ---- persistence ----

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		items = NonNullList.withSize(spec.size(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, items, registries);
		energy.setEnergyStored(tag.getInt("Energy"));
		progress = tag.getInt("Progress");
		maxProgress = tag.getInt("MaxProgress");
		extra = tag.getInt("Extra");
		redstoneMode = RedstoneMode.byId(tag.getInt("Redstone"));
		autoEject = !tag.contains("AutoEject") || tag.getBoolean("AutoEject");
		if (tag.contains("Sides")) {
			int[] stored = tag.getIntArray("Sides");
			for (int i = 0; i < Math.min(6, stored.length); i++) {
				sides[i] = SideMode.byId(stored[i]);
			}
		}
		loadExtra(tag, registries);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, items, registries);
		tag.putInt("Energy", energy.getEnergyStored());
		tag.putInt("Progress", progress);
		tag.putInt("MaxProgress", maxProgress);
		tag.putInt("Extra", extra);
		tag.putInt("Redstone", redstoneMode.ordinal());
		tag.putBoolean("AutoEject", autoEject);
		int[] stored = new int[6];
		for (int i = 0; i < 6; i++) {
			stored[i] = sides[i].ordinal();
		}
		tag.putIntArray("Sides", stored);
		saveExtra(tag, registries);
	}

	protected void loadExtra(CompoundTag tag, HolderLookup.Provider registries) {
	}

	protected void saveExtra(CompoundTag tag, HolderLookup.Provider registries) {
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Energy", energy.getEnergyStored());
		return tag;
	}

	/** Items dropped when the block is broken (everything in the inventory). */
	public NonNullList<ItemStack> getDrops() {
		return items;
	}

	@Nullable
	protected static Level levelOf(BlockEntity entity) {
		return entity.getLevel();
	}
}
