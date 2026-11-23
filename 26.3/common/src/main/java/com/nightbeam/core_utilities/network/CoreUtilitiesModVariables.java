package com.nightbeam.core_utilities.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class CoreUtilitiesModVariables {
	public static void setClientMapVariables(MapVariables data) {
		MapVariables.clientSide = data;
	}

	public static void setClientWorldVariables(WorldVariables data) {
		WorldVariables.clientSide = data;
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "core_utilities_worldvars";
		public static final Codec<WorldVariables> CODEC = Codec.BOOL.xmap(b -> new WorldVariables(), v -> false);
		public static final SavedDataType<WorldVariables> TYPE = new SavedDataType<>(
				Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, "worldvars"), WorldVariables::new, CODEC, DataFixTypes.LEVEL);

		public static WorldVariables load(CompoundTag tag) {
			return new WorldVariables();
		}

		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(TYPE);
			}
			return clientSide;
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "core_utilities_mapvars";
		public double forgeFlow = 0;

		public static final Codec<MapVariables> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.DOUBLE.optionalFieldOf("forgeFlow", 0.0).forGetter(v -> v.forgeFlow)
		).apply(i, MapVariables::new));

		public static final SavedDataType<MapVariables> TYPE = new SavedDataType<>(
				Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, "mapvars"), MapVariables::new, CODEC, DataFixTypes.LEVEL);

		public MapVariables() {
		}

		public MapVariables(double forgeFlow) {
			this.forgeFlow = forgeFlow;
		}

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.forgeFlow = tag.getDouble("forgeFlow").orElse(0.0);
			return data;
		}

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
				ServerLevel overworld = serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD);
				if (overworld != null) {
					return overworld.getDataStorage().computeIfAbsent(TYPE);
				}
			}
			return clientSide;
		}
	}
}
