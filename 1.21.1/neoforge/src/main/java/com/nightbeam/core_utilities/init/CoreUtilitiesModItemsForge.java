package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.item.*;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Loader specific items (the backpacks use NeoForge menu extensions). Everything else is registered in common. */
public class CoreUtilitiesModItemsForge {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, CoreUtilitiesMod.MOD_ID);
	public static final DeferredHolder<Item, Item> SMALL_BACKPACK = REGISTRY.register("small_backpack", () -> new SmallBackpackItem());
	public static final DeferredHolder<Item, Item> MEDIUM_BACKPACK = REGISTRY.register("medium_backpack", () -> new MediumBackpackItem());
	public static final DeferredHolder<Item, Item> BIG_BACKPACK = REGISTRY.register("big_backpack", () -> new BigBackpackItem());

	static {
		CoreUtilitiesModItems.SMALL_BACKPACK = () -> SMALL_BACKPACK.get();
		CoreUtilitiesModItems.MEDIUM_BACKPACK = () -> MEDIUM_BACKPACK.get();
		CoreUtilitiesModItems.BIG_BACKPACK = () -> BIG_BACKPACK.get();
	}
}
