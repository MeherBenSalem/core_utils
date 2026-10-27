package com.nightbeam.core_utilities.network;

import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

public class CoreUtilitiesModVariables {
	public static void setClientMapVariables(MapVariables data) {
		MapVariables.clientSide = data;
	}

	public static void setClientWorldVariables(WorldVariables data) {
		WorldVariables.clientSide = data;
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "core_utilities_worldvars";

		public static WorldVariables load(CompoundTag tag, HolderLookup.Provider registries) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WorldVariables::new, WorldVariables::load, DataFixTypes.LEVEL), DATA_NAME);
			}
			return clientSide;
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "core_utilities_mapvars";

		public static MapVariables load(CompoundTag tag, HolderLookup.Provider registries) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			Services.PLATFORM.syncMapVariables(world, this);
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage()
						.computeIfAbsent(new SavedData.Factory<>(MapVariables::new, MapVariables::load, DataFixTypes.LEVEL), DATA_NAME);
			}
			return clientSide;
		}
	}
}
