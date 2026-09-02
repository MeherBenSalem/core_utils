package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.block.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CoreUtilitiesModBlockEntitiesFabric {
	public static void register() {
	}

	private static ResourceLocation id(String path) {
		return new ResourceLocation(CoreUtilitiesMod.MOD_ID, path);
	}
}
