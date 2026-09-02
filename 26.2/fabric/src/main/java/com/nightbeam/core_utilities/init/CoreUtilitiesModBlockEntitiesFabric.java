package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.block.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CoreUtilitiesModBlockEntitiesFabric {
	public static void register() {
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, path);
	}
}
