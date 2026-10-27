package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.CoreUtilitiesModForge;
import com.nightbeam.core_utilities.init.ScreenAccessor;
import com.nightbeam.core_utilities.network.MenuStateUpdateMessage;
import com.nightbeam.core_utilities.platform.services.IPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Consumer;

public class ForgePlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "Forge";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FMLLoader.getLoadingModList().getModFileById(modId) != null;
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public void sendMenuUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
		if (player instanceof ServerPlayer serverPlayer) {
			CoreUtilitiesModForge.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
					new MenuStateUpdateMessage(elementType, name, elementState));
		} else if (player.level().isClientSide) {
			if (needClientUpdate && Minecraft.getInstance().screen instanceof ScreenAccessor accessor) {
				accessor.updateMenuState(elementType, name, elementState);
			}
			CoreUtilitiesModForge.PACKET_HANDLER.sendToServer(new MenuStateUpdateMessage(elementType, name, elementState));
		}
	}

	@Override
	public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter) {
		NetworkHooks.openScreen(player, menuProvider, extraDataWriter);
	}

	@Override
	public CompoundTag getPersistentData(Entity entity) {
		return entity.getPersistentData();
	}

	@Override
	public boolean isBlockInTag(net.minecraft.world.level.block.state.BlockState state, String tagPath) {
		net.minecraft.resources.ResourceLocation location = net.minecraft.resources.ResourceLocation.tryParse(tagPath.toLowerCase(java.util.Locale.ENGLISH));
		if (location == null) {
			return false;
		}
		return state.is(net.minecraft.tags.BlockTags.create(location));
	}

	@Override
	public CompoundTag getBlockEntityData(BlockEntity blockEntity) {
		return blockEntity.getPersistentData();
	}

	@Override
	public void syncMapVariables(net.minecraft.world.level.LevelAccessor world, com.nightbeam.core_utilities.network.CoreUtilitiesModVariables.MapVariables data) {
		com.nightbeam.core_utilities.network.CoreUtilitiesModNetwork.syncMapData(world, data);
	}

	private static final java.util.Map<net.minecraft.resources.ResourceKey<?>, net.minecraftforge.registries.DeferredRegister<?>> REGISTERS = new java.util.LinkedHashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <R, T extends R> java.util.function.Supplier<T> register(net.minecraft.core.Registry<R> registry, String name, java.util.function.Supplier<T> factory) {
		net.minecraftforge.registries.DeferredRegister<R> register = (net.minecraftforge.registries.DeferredRegister<R>) REGISTERS.computeIfAbsent(registry.key(),
				key -> net.minecraftforge.registries.DeferredRegister.create(registry.key(), com.nightbeam.core_utilities.CoreUtilitiesMod.MOD_ID));
		return register.register(name, factory);
	}

	/** Attaches every deferred register created through {@link #register} to the mod event bus. */
	public static void attachRegisters(net.minecraftforge.eventbus.api.IEventBus bus) {
		REGISTERS.values().forEach(register -> register.register(bus));
	}

	@Override
	public void invalidateCapabilities(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity != null) {
			// Invalidates the cached LazyOptionals handed to neighbours; the provider rebuilds them on the next query
			blockEntity.invalidateCaps();
			blockEntity.reviveCaps();
		}
	}

	@Override
	public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(
			java.util.function.BiFunction<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState, T> factory, net.minecraft.world.level.block.Block... blocks) {
		return net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(factory::apply, blocks).build(null);
	}

	@Override
	public <M extends net.minecraft.world.inventory.AbstractContainerMenu> net.minecraft.world.inventory.MenuType<M> createMenuType(
			java.util.function.BiFunction<Integer, net.minecraft.world.entity.player.Inventory, M> factory) {
		return new net.minecraft.world.inventory.MenuType<>(factory::apply, net.minecraft.world.flag.FeatureFlags.VANILLA_SET);
	}

	@Override
	public java.nio.file.Path getConfigDir() {
		return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
	}

	@Override
	public int getBurnTime(ItemStack stack) {
		return stack.isEmpty() ? 0 : net.minecraftforge.common.ForgeHooks.getBurnTime(stack, net.minecraft.world.item.crafting.RecipeType.SMELTING);
	}

	@Override
	public ItemStackHandlerAccess getBackpackInventory(ItemStack stack) {
		IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseGet(() -> new ItemStackHandler(27));
		return new ItemStackHandlerAccess() {
			@Override
			public int getSlots() {
				return handler.getSlots();
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				return handler.getStackInSlot(slot);
			}

			@Override
			public void setStackInSlot(int slot, ItemStack stack) {
				if (handler instanceof IItemHandlerModifiable modifiable) {
					modifiable.setStackInSlot(slot, stack);
				}
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				return handler.insertItem(slot, stack, simulate);
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return handler.extractItem(slot, amount, simulate);
			}

			@Override
			public CompoundTag serializeNBT() {
				return handler instanceof ItemStackHandler itemStackHandler ? itemStackHandler.serializeNBT() : new CompoundTag();
			}

			@Override
			public void deserializeNBT(CompoundTag tag) {
				if (handler instanceof ItemStackHandler itemStackHandler) {
					itemStackHandler.deserializeNBT(tag);
				}
			}
		};
	}
}


