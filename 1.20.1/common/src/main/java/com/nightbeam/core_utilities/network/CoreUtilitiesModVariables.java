package com.nightbeam.core_utilities.network;

import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.nbt.CompoundTag;
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

		public static WorldVariables load(CompoundTag tag) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(WorldVariables::load, WorldVariables::new, DATA_NAME);
			}
			return clientSide;
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "core_utilities_mapvars";
		public double forgeFlow = 0;

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
			forgeFlow = nbt.getDouble("forgeFlow");
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			nbt.putDouble("forgeFlow", forgeFlow);
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
						.computeIfAbsent(MapVariables::load, MapVariables::new, DATA_NAME);
			}
			return clientSide;
		}
	}
}
