package com.nightbeam.core_utilities.init;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Loader specific items (the backpacks use Forge item capabilities). Everything else is registered in common. */
public class CoreUtilitiesModItemsForge {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CoreUtilitiesMod.MOD_ID);
	public static final RegistryObject<Item> SMALL_BACKPACK = REGISTRY.register("small_backpack", () -> new SmallBackpackItem());
	public static final RegistryObject<Item> MEDIUM_BACKPACK = REGISTRY.register("medium_backpack", () -> new MediumBackpackItem());
	public static final RegistryObject<Item> BIG_BACKPACK = REGISTRY.register("big_backpack", () -> new BigBackpackItem());

	static {
		CoreUtilitiesModItems.SMALL_BACKPACK = () -> SMALL_BACKPACK.get();
		CoreUtilitiesModItems.MEDIUM_BACKPACK = () -> MEDIUM_BACKPACK.get();
		CoreUtilitiesModItems.BIG_BACKPACK = () -> BIG_BACKPACK.get();
	}
}
