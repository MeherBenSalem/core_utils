package com.nightbeam.core_utilities.world.inventory;

import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.nightbeam.core_utilities.init.CoreUtilitiesModMenus;

public class BigBackpackGUIMenu extends AbstractContainerMenu implements com.nightbeam.core_utilities.init.MenuAccessor {
	public final Map<String, Object> menuState = new HashMap<>() {
		@Override
		public Object put(String key, Object value) {
			if (!this.containsKey(key) && this.size() >= 100)
				return null;
			return super.put(key, value);
		}
	};
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private Supplier<Boolean> boundItemMatcher = null;
	private Entity boundEntity = null;
	private BlockEntity boundBlockEntity = null;

	public BigBackpackGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(CoreUtilitiesModMenus.BIG_BACKPACK_GUI.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(100);
		BlockPos pos = null;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}
		if (pos != null) {
			if (extraData.readableBytes() == 1) { // bound to item
				byte hand = extraData.readByte();
				ItemStack itemstack = hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem();
				this.boundItemMatcher = () -> itemstack == (hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem());
				itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
					this.internal = capability;
					this.bound = true;
				});
			} else if (extraData.readableBytes() > 1) { // bound to entity
				extraData.readByte(); // drop padding
				boundEntity = world.getEntity(extraData.readVarInt());
				if (boundEntity != null)
					boundEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
						this.internal = capability;
						this.bound = true;
					});
			} else { // might be bound to block
				boundBlockEntity = this.world.getBlockEntity(pos);
				if (boundBlockEntity != null)
					boundBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
						this.internal = capability;
						this.bound = true;
					});
			}
		}
		this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 8, 7) {
			private final int slot = 0;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 26, 7) {
			private final int slot = 1;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 44, 7) {
			private final int slot = 2;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 62, 7) {
			private final int slot = 3;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 80, 7) {
			private final int slot = 4;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 98, 7) {
			private final int slot = 5;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(6, this.addSlot(new SlotItemHandler(internal, 6, 116, 7) {
			private final int slot = 6;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(7, this.addSlot(new SlotItemHandler(internal, 7, 134, 7) {
			private final int slot = 7;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(8, this.addSlot(new SlotItemHandler(internal, 8, 152, 7) {
			private final int slot = 8;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(9, this.addSlot(new SlotItemHandler(internal, 9, 170, 7) {
			private final int slot = 9;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(10, this.addSlot(new SlotItemHandler(internal, 10, 188, 7) {
			private final int slot = 10;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(11, this.addSlot(new SlotItemHandler(internal, 11, 206, 7) {
			private final int slot = 11;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(12, this.addSlot(new SlotItemHandler(internal, 12, 224, 7) {
			private final int slot = 12;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(13, this.addSlot(new SlotItemHandler(internal, 13, 242, 7) {
			private final int slot = 13;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(14, this.addSlot(new SlotItemHandler(internal, 14, 8, 25) {
			private final int slot = 14;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(15, this.addSlot(new SlotItemHandler(internal, 15, 26, 25) {
			private final int slot = 15;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(16, this.addSlot(new SlotItemHandler(internal, 16, 44, 25) {
			private final int slot = 16;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(17, this.addSlot(new SlotItemHandler(internal, 17, 62, 25) {
			private final int slot = 17;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(18, this.addSlot(new SlotItemHandler(internal, 18, 80, 25) {
			private final int slot = 18;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(19, this.addSlot(new SlotItemHandler(internal, 19, 98, 25) {
			private final int slot = 19;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(20, this.addSlot(new SlotItemHandler(internal, 20, 116, 25) {
			private final int slot = 20;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(21, this.addSlot(new SlotItemHandler(internal, 21, 134, 25) {
			private final int slot = 21;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(22, this.addSlot(new SlotItemHandler(internal, 22, 152, 25) {
			private final int slot = 22;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(23, this.addSlot(new SlotItemHandler(internal, 23, 170, 25) {
			private final int slot = 23;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(24, this.addSlot(new SlotItemHandler(internal, 24, 188, 25) {
			private final int slot = 24;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(25, this.addSlot(new SlotItemHandler(internal, 25, 206, 25) {
			private final int slot = 25;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(26, this.addSlot(new SlotItemHandler(internal, 26, 224, 25) {
			private final int slot = 26;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(27, this.addSlot(new SlotItemHandler(internal, 27, 242, 25) {
			private final int slot = 27;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(28, this.addSlot(new SlotItemHandler(internal, 28, 8, 43) {
			private final int slot = 28;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(29, this.addSlot(new SlotItemHandler(internal, 29, 26, 43) {
			private final int slot = 29;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(30, this.addSlot(new SlotItemHandler(internal, 30, 44, 43) {
			private final int slot = 30;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(31, this.addSlot(new SlotItemHandler(internal, 31, 62, 43) {
			private final int slot = 31;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(32, this.addSlot(new SlotItemHandler(internal, 32, 80, 43) {
			private final int slot = 32;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(33, this.addSlot(new SlotItemHandler(internal, 33, 98, 43) {
			private final int slot = 33;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(34, this.addSlot(new SlotItemHandler(internal, 34, 116, 43) {
			private final int slot = 34;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(35, this.addSlot(new SlotItemHandler(internal, 35, 134, 43) {
			private final int slot = 35;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(36, this.addSlot(new SlotItemHandler(internal, 36, 152, 43) {
			private final int slot = 36;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(37, this.addSlot(new SlotItemHandler(internal, 37, 170, 43) {
			private final int slot = 37;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(38, this.addSlot(new SlotItemHandler(internal, 38, 188, 43) {
			private final int slot = 38;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(39, this.addSlot(new SlotItemHandler(internal, 39, 206, 43) {
			private final int slot = 39;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(40, this.addSlot(new SlotItemHandler(internal, 40, 224, 43) {
			private final int slot = 40;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(41, this.addSlot(new SlotItemHandler(internal, 41, 242, 43) {
			private final int slot = 41;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(42, this.addSlot(new SlotItemHandler(internal, 42, 8, 61) {
			private final int slot = 42;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(43, this.addSlot(new SlotItemHandler(internal, 43, 26, 61) {
			private final int slot = 43;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(44, this.addSlot(new SlotItemHandler(internal, 44, 44, 61) {
			private final int slot = 44;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(45, this.addSlot(new SlotItemHandler(internal, 45, 62, 61) {
			private final int slot = 45;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(46, this.addSlot(new SlotItemHandler(internal, 46, 80, 61) {
			private final int slot = 46;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(47, this.addSlot(new SlotItemHandler(internal, 47, 98, 61) {
			private final int slot = 47;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(48, this.addSlot(new SlotItemHandler(internal, 48, 116, 61) {
			private final int slot = 48;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(49, this.addSlot(new SlotItemHandler(internal, 49, 134, 61) {
			private final int slot = 49;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(50, this.addSlot(new SlotItemHandler(internal, 50, 152, 61) {
			private final int slot = 50;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(51, this.addSlot(new SlotItemHandler(internal, 51, 170, 61) {
			private final int slot = 51;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(52, this.addSlot(new SlotItemHandler(internal, 52, 188, 61) {
			private final int slot = 52;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(53, this.addSlot(new SlotItemHandler(internal, 53, 206, 61) {
			private final int slot = 53;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(54, this.addSlot(new SlotItemHandler(internal, 54, 224, 61) {
			private final int slot = 54;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(55, this.addSlot(new SlotItemHandler(internal, 55, 242, 61) {
			private final int slot = 55;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(56, this.addSlot(new SlotItemHandler(internal, 56, 8, 79) {
			private final int slot = 56;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(57, this.addSlot(new SlotItemHandler(internal, 57, 26, 79) {
			private final int slot = 57;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(58, this.addSlot(new SlotItemHandler(internal, 58, 44, 79) {
			private final int slot = 58;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(59, this.addSlot(new SlotItemHandler(internal, 59, 62, 79) {
			private final int slot = 59;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(60, this.addSlot(new SlotItemHandler(internal, 60, 80, 79) {
			private final int slot = 60;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(61, this.addSlot(new SlotItemHandler(internal, 61, 98, 79) {
			private final int slot = 61;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(62, this.addSlot(new SlotItemHandler(internal, 62, 116, 79) {
			private final int slot = 62;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(63, this.addSlot(new SlotItemHandler(internal, 63, 134, 79) {
			private final int slot = 63;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(64, this.addSlot(new SlotItemHandler(internal, 64, 152, 79) {
			private final int slot = 64;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(65, this.addSlot(new SlotItemHandler(internal, 65, 170, 79) {
			private final int slot = 65;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(66, this.addSlot(new SlotItemHandler(internal, 66, 188, 79) {
			private final int slot = 66;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(67, this.addSlot(new SlotItemHandler(internal, 67, 206, 79) {
			private final int slot = 67;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(68, this.addSlot(new SlotItemHandler(internal, 68, 224, 79) {
			private final int slot = 68;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(69, this.addSlot(new SlotItemHandler(internal, 69, 242, 79) {
			private final int slot = 69;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(70, this.addSlot(new SlotItemHandler(internal, 70, 8, 97) {
			private final int slot = 70;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(71, this.addSlot(new SlotItemHandler(internal, 71, 26, 97) {
			private final int slot = 71;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(72, this.addSlot(new SlotItemHandler(internal, 72, 44, 97) {
			private final int slot = 72;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(73, this.addSlot(new SlotItemHandler(internal, 73, 62, 97) {
			private final int slot = 73;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(74, this.addSlot(new SlotItemHandler(internal, 74, 80, 97) {
			private final int slot = 74;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(75, this.addSlot(new SlotItemHandler(internal, 75, 98, 97) {
			private final int slot = 75;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(76, this.addSlot(new SlotItemHandler(internal, 76, 116, 97) {
			private final int slot = 76;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(77, this.addSlot(new SlotItemHandler(internal, 77, 134, 97) {
			private final int slot = 77;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(78, this.addSlot(new SlotItemHandler(internal, 78, 152, 97) {
			private final int slot = 78;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(79, this.addSlot(new SlotItemHandler(internal, 79, 170, 97) {
			private final int slot = 79;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(80, this.addSlot(new SlotItemHandler(internal, 80, 188, 97) {
			private final int slot = 80;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(81, this.addSlot(new SlotItemHandler(internal, 81, 206, 97) {
			private final int slot = 81;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(82, this.addSlot(new SlotItemHandler(internal, 82, 224, 97) {
			private final int slot = 82;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(83, this.addSlot(new SlotItemHandler(internal, 83, 242, 97) {
			private final int slot = 83;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(84, this.addSlot(new SlotItemHandler(internal, 84, 8, 115) {
			private final int slot = 84;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(85, this.addSlot(new SlotItemHandler(internal, 85, 26, 115) {
			private final int slot = 85;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(86, this.addSlot(new SlotItemHandler(internal, 86, 224, 115) {
			private final int slot = 86;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(87, this.addSlot(new SlotItemHandler(internal, 87, 242, 115) {
			private final int slot = 87;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(88, this.addSlot(new SlotItemHandler(internal, 88, 8, 133) {
			private final int slot = 88;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(89, this.addSlot(new SlotItemHandler(internal, 89, 26, 133) {
			private final int slot = 89;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(90, this.addSlot(new SlotItemHandler(internal, 90, 224, 133) {
			private final int slot = 90;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(91, this.addSlot(new SlotItemHandler(internal, 91, 242, 133) {
			private final int slot = 91;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(92, this.addSlot(new SlotItemHandler(internal, 92, 8, 151) {
			private final int slot = 92;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(93, this.addSlot(new SlotItemHandler(internal, 93, 26, 151) {
			private final int slot = 93;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(94, this.addSlot(new SlotItemHandler(internal, 94, 224, 151) {
			private final int slot = 94;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(95, this.addSlot(new SlotItemHandler(internal, 95, 242, 151) {
			private final int slot = 95;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(96, this.addSlot(new SlotItemHandler(internal, 96, 8, 169) {
			private final int slot = 96;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(97, this.addSlot(new SlotItemHandler(internal, 97, 26, 169) {
			private final int slot = 97;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(98, this.addSlot(new SlotItemHandler(internal, 98, 224, 169) {
			private final int slot = 98;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		this.customSlots.put(99, this.addSlot(new SlotItemHandler(internal, 99, 242, 169) {
			private final int slot = 99;
			private int x = BigBackpackGUIMenu.this.x;
			private int y = BigBackpackGUIMenu.this.y;
		}));
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 45 + 8 + sj * 18, 40 + 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 45 + 8 + si * 18, 40 + 142));
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.bound) {
			if (this.boundItemMatcher != null)
				return this.boundItemMatcher.get();
			else if (this.boundBlockEntity != null)
				return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
			else if (this.boundEntity != null)
				return this.boundEntity.isAlive();
		}
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 100) {
				if (!this.moveItemStackTo(itemstack1, 100, this.slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!this.moveItemStackTo(itemstack1, 0, 100, false)) {
				if (index < 100 + 27) {
					if (!this.moveItemStackTo(itemstack1, 100 + 27, this.slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!this.moveItemStackTo(itemstack1, 100, 100 + 27, false))
						return ItemStack.EMPTY;
				}
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@Override
	protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		boolean flag = false;
		int i = p_38905_;
		if (p_38907_) {
			i = p_38906_ - 1;
		}
		if (p_38904_.isStackable()) {
			while (!p_38904_.isEmpty()) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot = this.slots.get(i);
				ItemStack itemstack = slot.getItem();
				if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
					int j = itemstack.getCount() + p_38904_.getCount();
					int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
					if (j <= maxSize) {
						p_38904_.setCount(0);
						itemstack.setCount(j);
						slot.set(itemstack);
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						p_38904_.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.set(itemstack);
						flag = true;
					}
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		if (!p_38904_.isEmpty()) {
			if (p_38907_) {
				i = p_38906_ - 1;
			} else {
				i = p_38905_;
			}
			while (true) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot1 = this.slots.get(i);
				ItemStack itemstack1 = slot1.getItem();
				if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
					if (p_38904_.getCount() > slot1.getMaxStackSize()) {
						slot1.setByPlayer(p_38904_.split(slot1.getMaxStackSize()));
					} else {
						slot1.setByPlayer(p_38904_.split(p_38904_.getCount()));
					}
					slot1.setChanged();
					flag = true;
					break;
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		return flag;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
			if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
				for (int j = 0; j < internal.getSlots(); ++j) {
					playerIn.drop(internal.getStackInSlot(j), false);
					if (internal instanceof IItemHandlerModifiable ihm)
						ihm.setStackInSlot(j, ItemStack.EMPTY);
				}
			} else {
				for (int i = 0; i < internal.getSlots(); ++i) {
					playerIn.getInventory().placeItemBackInInventory(internal.getStackInSlot(i));
					if (internal instanceof IItemHandlerModifiable ihm)
						ihm.setStackInSlot(i, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public Map<Integer, Slot> getSlots() {
		return Collections.unmodifiableMap(customSlots);
	}

	@Override
	public Map<String, Object> getMenuState() {
		return menuState;
	}
}

