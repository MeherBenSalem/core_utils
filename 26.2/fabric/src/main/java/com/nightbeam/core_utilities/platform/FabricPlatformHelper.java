package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.init.ScreenAccessor;
import com.nightbeam.core_utilities.network.CoreUtilitiesModNetworkFabric;
import com.nightbeam.core_utilities.network.FabricMenuStateUpdatePacket;
import com.nightbeam.core_utilities.platform.services.IPlatformHelper;
import com.nightbeam.core_utilities.util.BlockEntityData;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class FabricPlatformHelper implements IPlatformHelper {
	private static final Map<Entity, CompoundTag> PERSISTENT_DATA = new WeakHashMap<>();

	@Override
	public String getPlatformName() {
		return "Fabric";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public void sendMenuUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
		FabricMenuStateUpdatePacket payload = new FabricMenuStateUpdatePacket(elementType, name, elementState);
		if (player instanceof ServerPlayer serverPlayer) {
			ServerPlayNetworking.send(serverPlayer, payload);
		} else if (player.level().isClientSide()) {
			ClientPlayNetworking.send(payload);
			if (needClientUpdate && Minecraft.getInstance().gui.screen() instanceof ScreenAccessor accessor) {
				accessor.updateMenuState(elementType, name, elementState);
			}
		}
	}

	@Override
	public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		extraDataWriter.accept(buf);
		BlockPos pos = buf.readBlockPos();
		player.openMenu(new ExtendedMenuProvider<BlockPos>() {
			@Override
			public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int syncId,
					net.minecraft.world.entity.player.Inventory inv, net.minecraft.world.entity.player.Player player) {
				return menuProvider.createMenu(syncId, inv, player);
			}

			@Override
			public net.minecraft.network.chat.Component getDisplayName() {
				return menuProvider.getDisplayName();
			}

			@Override
			public BlockPos getScreenOpeningData(ServerPlayer player) {
				return pos;
			}
		});
	}

	@Override
	public CompoundTag getPersistentData(Entity entity) {
		return PERSISTENT_DATA.computeIfAbsent(entity, e -> new CompoundTag());
	}

	@Override
	public boolean isBlockInTag(BlockState state, String tagPath) {
		Identifier location = Identifier.tryParse(tagPath.toLowerCase(java.util.Locale.ENGLISH));
		return location != null && state.is(TagKey.create(Registries.BLOCK, location));
	}

	@Override
	public CompoundTag getBlockEntityData(BlockEntity blockEntity) {
		return BlockEntityData.get(blockEntity);
	}

	@Override
	public void syncMapVariables(net.minecraft.world.level.LevelAccessor world, com.nightbeam.core_utilities.network.CoreUtilitiesModVariables.MapVariables data) {
		CoreUtilitiesModNetworkFabric.syncMapData(world, data);
	}

	@Override
	public <R, T extends R> java.util.function.Supplier<T> register(net.minecraft.core.Registry<R> registry, String name, java.util.function.Supplier<T> factory) {
		T value = net.minecraft.core.Registry.register(registry, Identifier.fromNamespaceAndPath(com.nightbeam.core_utilities.CoreUtilitiesMod.MOD_ID, name), factory.get());
		return () -> value;
	}

	@Override
	public <M extends net.minecraft.world.inventory.AbstractContainerMenu> net.minecraft.world.inventory.MenuType<M> createMenuType(
			java.util.function.BiFunction<Integer, net.minecraft.world.entity.player.Inventory, M> factory) {
		return new net.minecraft.world.inventory.MenuType<>(factory::apply, net.minecraft.world.flag.FeatureFlags.VANILLA_SET);
	}

	@Override
	public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(
			java.util.function.BiFunction<BlockPos, BlockState, T> factory, net.minecraft.world.level.block.Block... blocks) {
		return new net.minecraft.world.level.block.entity.BlockEntityType<>(factory::apply, java.util.Set.of(blocks));
	}

	@Override
	public java.nio.file.Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public int getBurnTime(net.minecraft.world.level.Level level, ItemStack stack) {
		return stack.isEmpty() ? 0 : level.fuelValues().burnDuration(stack);
	}

	@Override
	public ItemStackHandlerAccess getBackpackInventory(ItemStack stack) {
		return new NbtBackpackInventory(stack);
	}
}
