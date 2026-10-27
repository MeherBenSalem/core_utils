package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.item.BigBackpackItem;
import com.nightbeam.core_utilities.item.MediumBackpackItem;
import com.nightbeam.core_utilities.item.SmallBackpackItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/** Loader specific items (the backpacks use Fabric screen handler extensions). Everything else is registered in common. */
public class CoreUtilitiesModItemsFabric {
	public static void register() {
		Item small_backpack = Registry.register(BuiltInRegistries.ITEM, id("small_backpack"), new SmallBackpackItem(key("small_backpack")));
		CoreUtilitiesModItems.SMALL_BACKPACK = () -> small_backpack;
		Item medium_backpack = Registry.register(BuiltInRegistries.ITEM, id("medium_backpack"), new MediumBackpackItem(key("medium_backpack")));
		CoreUtilitiesModItems.MEDIUM_BACKPACK = () -> medium_backpack;
		Item big_backpack = Registry.register(BuiltInRegistries.ITEM, id("big_backpack"), new BigBackpackItem(key("big_backpack")));
		CoreUtilitiesModItems.BIG_BACKPACK = () -> big_backpack;
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(CoreUtilitiesMod.MOD_ID, path);
	}

	private static ResourceKey<Item> key(String path) {
		return ResourceKey.create(Registries.ITEM, id(path));
	}
}
