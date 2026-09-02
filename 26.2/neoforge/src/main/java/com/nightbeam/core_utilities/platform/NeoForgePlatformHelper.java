package com.nightbeam.core_utilities.platform;

import com.nightbeam.core_utilities.init.ScreenAccessor;
import com.nightbeam.core_utilities.neoforge.NeoForgeNetworking;
import com.nightbeam.core_utilities.platform.services.IPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
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
		return true;
	}

	@Override
	public void sendMenuUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
		NeoForgeNetworking.MenuUpdatePayload payload = new NeoForgeNetworking.MenuUpdatePayload(elementType, name, elementState);
		if (player instanceof ServerPlayer serverPlayer) {
			PacketDistributor.sendToPlayer(serverPlayer, payload);
		} else if (player.level().isClientSide()) {
			if (needClientUpdate && Minecraft.getInstance().gui.screen() instanceof ScreenAccessor accessor) {
				accessor.updateMenuState(elementType, name, elementState);
			}
			ClientPacketDistributor.sendToServer(payload);
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
		Identifier location = Identifier.tryParse(tagPath.toLowerCase(java.util.Locale.ENGLISH));
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

	@Override
	public ItemStackHandlerAccess getBackpackInventory(ItemStack stack) {
		return new NbtBackpackInventory(stack);
	}
}