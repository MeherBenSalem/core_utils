package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.init.ScreenAccessor;
import com.nightbeam.core_utilities.neoforge.NeoForgeNetworking;
import com.nightbeam.core_utilities.platform.services.IPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Consumer;

public class NeoForgePlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "NeoForge";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public void sendMenuUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
		NeoForgeNetworking.MenuUpdatePayload payload = new NeoForgeNetworking.MenuUpdatePayload(elementType, name, elementState);
		if (player instanceof ServerPlayer serverPlayer) {
			PacketDistributor.sendToPlayer(serverPlayer, payload);
		} else if (player.level().isClientSide) {
			if (needClientUpdate && Minecraft.getInstance().screen instanceof ScreenAccessor accessor) {
				accessor.updateMenuState(elementType, name, elementState);
			}
			PacketDistributor.sendToServer(payload);
		}
	}

	@Override
	public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter) {
		player.openMenu(menuProvider, (RegistryFriendlyByteBuf buf) -> extraDataWriter.accept(buf));
	}

	@Override
	public CompoundTag getPersistentData(Entity entity) {
		return entity.getPersistentData();
	}

	@Override
	public boolean isBlockInTag(BlockState state, String tagPath) {
		ResourceLocation location = ResourceLocation.tryParse(tagPath.toLowerCase(java.util.Locale.ENGLISH));
		if (location == null) {
			return false;
		}
		return state.is(TagKey.create(Registries.BLOCK, location));
	}

	@Override
	public CompoundTag getBlockEntityData(BlockEntity blockEntity) {
		return blockEntity.getPersistentData();
	}

	@Override
	public void syncMapVariables(net.minecraft.world.level.LevelAccessor world, com.nightbeam.core_utilities.network.CoreUtilitiesModVariables.MapVariables data) {
		NeoForgeNetworking.syncMapData(world, data);
	}

	private static final java.util.Map<net.minecraft.resources.ResourceKey<?>, net.neoforged.neoforge.registries.DeferredRegister<?>> REGISTERS = new java.util.LinkedHashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <R, T extends R> java.util.function.Supplier<T> register(net.minecraft.core.Registry<R> registry, String name, java.util.function.Supplier<T> factory) {
		net.neoforged.neoforge.registries.DeferredRegister<R> register = (net.neoforged.neoforge.registries.DeferredRegister<R>) REGISTERS.computeIfAbsent(registry.key(),
				key -> net.neoforged.neoforge.registries.DeferredRegister.create(registry, com.nightbeam.core_utilities.CoreUtilitiesMod.MOD_ID));
		return register.register(name, factory);
	}

	/** Attaches every deferred register created through {@link #register} to the mod event bus. */
	public static void attachRegisters(net.neoforged.bus.api.IEventBus bus) {
		REGISTERS.values().forEach(register -> register.register(bus));
	}

	@Override
	public void invalidateCapabilities(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
		level.invalidateCapabilities(pos);
	}

	@Override
	public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(
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
		return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
	}

	@Override
	public int getBurnTime(ItemStack stack) {
		return stack.isEmpty() ? 0 : stack.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING);
	}

	@Override
	public ItemStackHandlerAccess getBackpackInventory(ItemStack stack) {
		return new NbtBackpackInventory(stack);
	}
}
