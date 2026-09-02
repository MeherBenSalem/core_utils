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


