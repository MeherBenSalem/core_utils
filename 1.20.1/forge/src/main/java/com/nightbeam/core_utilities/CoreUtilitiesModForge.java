package com.nightbeam.core_utilities;

import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.energy.EnergyHandlerProvider;
import com.nightbeam.core_utilities.init.CoreUtilitiesModItemsForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModMenusForge;
import com.nightbeam.core_utilities.init.CoreUtilitiesModTabs;
import com.nightbeam.core_utilities.network.CoreUtilitiesModNetwork;
import com.nightbeam.core_utilities.network.MenuStateUpdateMessage;
import com.nightbeam.core_utilities.machine.MachineBlockEntity;
import com.nightbeam.core_utilities.platform.ForgeEnergyStorage;
import com.nightbeam.core_utilities.platform.ForgePlatformHelper;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(CoreUtilitiesMod.MOD_ID)
public class CoreUtilitiesModForge {
	public static final Logger LOGGER = LogManager.getLogger(CoreUtilitiesModForge.class);

	public CoreUtilitiesModForge() {
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		CoreUtilitiesMod.init();
		ForgePlatformHelper.attachRegisters(bus);
		CoreUtilitiesModItemsForge.REGISTRY.register(bus);
		CoreUtilitiesModTabs.REGISTRY.register(bus);
		CoreUtilitiesModMenusForge.REGISTRY.register(bus);
		bus.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, CoreUtilitiesModForge::attachCapabilities);
	}

	/** Exposes Forge Energy and item handlers for every Core Utilities energy block. */
	private static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
		if (event.getObject() instanceof EnergyHandlerProvider) {
			MachineCapabilities provider = new MachineCapabilities(event.getObject());
			event.addCapability(new ResourceLocation(CoreUtilitiesMod.MOD_ID, "machine"), provider);
			event.addListener(provider::invalidate);
		}
	}

	/** Lazily built per-face capabilities; invalidated when the block entity is removed or its faces change. */
	private static class MachineCapabilities implements ICapabilityProvider {
		private final BlockEntity entity;
		private final java.util.Map<Direction, LazyOptional<?>> energy = new EnumMap<>(Direction.class);
		private final java.util.Map<Direction, LazyOptional<?>> items = new EnumMap<>(Direction.class);
		private LazyOptional<?> energyNoSide;
		private LazyOptional<?> itemsNoSide;

		MachineCapabilities(BlockEntity entity) {
			this.entity = entity;
		}

		@Override
		public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
			if (cap == ForgeCapabilities.ENERGY) {
				LazyOptional<?> optional = side == null ? energyNoSide : energy.get(side);
				if (optional == null) {
					EnergyHandler handler = ((EnergyHandlerProvider) entity).getEnergyHandler(side);
					optional = handler == null ? LazyOptional.empty() : LazyOptional.of(() -> ForgeEnergyStorage.expose(handler));
					if (side == null) {
						energyNoSide = optional;
					} else {
						energy.put(side, optional);
					}
				}
				return optional.cast();
			}
			if (cap == ForgeCapabilities.ITEM_HANDLER && entity instanceof MachineBlockEntity machine && machine.getContainerSize() > 0) {
				LazyOptional<?> optional = side == null ? itemsNoSide : items.get(side);
				if (optional == null) {
					optional = side == null ? LazyOptional.of(() -> new InvWrapper(machine)) : LazyOptional.of(() -> new SidedInvWrapper(machine, side));
					if (side == null) {
						itemsNoSide = optional;
					} else {
						items.put(side, optional);
					}
				}
				return optional.cast();
			}
			return LazyOptional.empty();
		}

		void invalidate() {
			energy.values().forEach(LazyOptional::invalidate);
			items.values().forEach(LazyOptional::invalidate);
			if (energyNoSide != null) {
				energyNoSide.invalidate();
			}
			if (itemsNoSide != null) {
				itemsNoSide.invalidate();
			}
			energy.clear();
			items.clear();
			energyNoSide = null;
			itemsNoSide = null;
		}
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			addNetworkMessage(MenuStateUpdateMessage.class, MenuStateUpdateMessage::buffer, MenuStateUpdateMessage::new,
					MenuStateUpdateMessage::handler);
			CoreUtilitiesModNetwork.register(event);
		});
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(CoreUtilitiesMod.MOD_ID, CoreUtilitiesMod.MOD_ID), () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder,
			Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0) {
					actions.add(work);
				}
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}
