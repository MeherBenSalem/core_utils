package com.nightbeam.core_utilities.platform.services;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface IPlatformHelper {
	String getPlatformName();

	boolean isModLoaded(String modId);

	boolean isDevelopmentEnvironment();

	default String getEnvironmentName() {
		return isDevelopmentEnvironment() ? "development" : "production";
	}

	void sendMenuUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate);

	void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter);

	CompoundTag getPersistentData(Entity entity);

	default CompoundTag getBlockEntityData(BlockEntity blockEntity) {
		return new CompoundTag();
	}

	default void syncMapVariables(net.minecraft.world.level.LevelAccessor world, com.nightbeam.core_utilities.network.CoreUtilitiesModVariables.MapVariables data) {
	}

	default boolean isBlockInTag(net.minecraft.world.level.block.state.BlockState state, String tagPath) {
		return false;
	}

	ItemStackHandlerAccess getBackpackInventory(ItemStack stack);

	/** Registers an object in a vanilla registry, deferred where the loader requires it. */
	<R, T extends R> java.util.function.Supplier<T> register(net.minecraft.core.Registry<R> registry, String name, java.util.function.Supplier<T> factory);

	java.nio.file.Path getConfigDir();

	/** Builds a menu type without extra client data (the vanilla constructor is not public outside the loaders). */
	<M extends net.minecraft.world.inventory.AbstractContainerMenu> net.minecraft.world.inventory.MenuType<M> createMenuType(
			java.util.function.BiFunction<Integer, net.minecraft.world.entity.player.Inventory, M> factory);

	/** Builds a block entity type (the vanilla factory interface is not public outside the loaders). */
	<T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(
			java.util.function.BiFunction<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState, T> factory, net.minecraft.world.level.block.Block... blocks);

	/** Drops cached capability lookups for a block whose exposed faces changed. */
	default void invalidateCapabilities(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
	}

	/** Furnace burn time in ticks, including fuels added by other mods. */
	int getBurnTime(ItemStack stack);

	interface ItemStackHandlerAccess {
		int getSlots();

		ItemStack getStackInSlot(int slot);

		void setStackInSlot(int slot, ItemStack stack);

		ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

		ItemStack extractItem(int slot, int amount, boolean simulate);

		CompoundTag serializeNBT();

		void deserializeNBT(CompoundTag tag);
	}
}
