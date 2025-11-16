/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.nightbeam.core_utilities.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import com.nightbeam.core_utilities.item.SmallBackpackItem;
import com.nightbeam.core_utilities.item.MobNetItem;
import com.nightbeam.core_utilities.item.MediumBackpackItem;
import com.nightbeam.core_utilities.item.ConfigurationWrenchItem;
import com.nightbeam.core_utilities.item.BlankPlateItem;
import com.nightbeam.core_utilities.item.BigBackpackItem;
import com.nightbeam.core_utilities.CoreUtilitiesMod;

public class CoreUtilitiesModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CoreUtilitiesMod.MODID);
	public static final RegistryObject<Item> VECTOR_PLATE_TIER_1 = block(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_1);
	public static final RegistryObject<Item> VECTOR_PLATE_TIER_2 = block(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_2);
	public static final RegistryObject<Item> VECTOR_PLATE_TIER_3 = block(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_3);
	public static final RegistryObject<Item> VECTOR_PLATE_TIER_4 = block(CoreUtilitiesModBlocks.VECTOR_PLATE_TIER_4);
	public static final RegistryObject<Item> BLANK_PLATE = REGISTRY.register("blank_plate", () -> new BlankPlateItem());
	public static final RegistryObject<Item> HEALING_PLATE = block(CoreUtilitiesModBlocks.HEALING_PLATE);
	public static final RegistryObject<Item> SPEED_PLATE = block(CoreUtilitiesModBlocks.SPEED_PLATE);
	public static final RegistryObject<Item> FIRE_RES_PLATE = block(CoreUtilitiesModBlocks.FIRE_RES_PLATE);
	public static final RegistryObject<Item> STRENGTH_PLATE = block(CoreUtilitiesModBlocks.STRENGTH_PLATE);
	public static final RegistryObject<Item> POISON_PLATE = block(CoreUtilitiesModBlocks.POISON_PLATE);
	public static final RegistryObject<Item> WEAKNESS_PLATE = block(CoreUtilitiesModBlocks.WEAKNESS_PLATE);
	public static final RegistryObject<Item> SLOWNESS_PLATE = block(CoreUtilitiesModBlocks.SLOWNESS_PLATE);
	public static final RegistryObject<Item> WITHER_PLATE = block(CoreUtilitiesModBlocks.WITHER_PLATE);
	public static final RegistryObject<Item> PLAYER_FILTER = block(CoreUtilitiesModBlocks.PLAYER_FILTER);
	public static final RegistryObject<Item> MOB_FILTER = block(CoreUtilitiesModBlocks.MOB_FILTER);
	public static final RegistryObject<Item> NEUTRAL_FILTER = block(CoreUtilitiesModBlocks.NEUTRAL_FILTER);
	public static final RegistryObject<Item> PROJECTILE_FILTER = block(CoreUtilitiesModBlocks.PROJECTILE_FILTER);
	public static final RegistryObject<Item> FILTER_ZOMBIE = block(CoreUtilitiesModBlocks.FILTER_ZOMBIE);
	public static final RegistryObject<Item> FILTER_SKELETON = block(CoreUtilitiesModBlocks.FILTER_SKELETON);
	public static final RegistryObject<Item> FILTER_SPIDER = block(CoreUtilitiesModBlocks.FILTER_SPIDER);
	public static final RegistryObject<Item> FILTER_VILLAGER = block(CoreUtilitiesModBlocks.FILTER_VILLAGER);
	public static final RegistryObject<Item> FILTER_PET = block(CoreUtilitiesModBlocks.FILTER_PET);
	public static final RegistryObject<Item> FILTER_ANIMAL = block(CoreUtilitiesModBlocks.FILTER_ANIMAL);
	public static final RegistryObject<Item> DENY_PLAYER_FILTER = block(CoreUtilitiesModBlocks.DENY_PLAYER_FILTER);
	public static final RegistryObject<Item> DENY_MOB_FILTER = block(CoreUtilitiesModBlocks.DENY_MOB_FILTER);
	public static final RegistryObject<Item> DENY_NEUTRAL_FILTER = block(CoreUtilitiesModBlocks.DENY_NEUTRAL_FILTER);
	public static final RegistryObject<Item> DENY_PROJECTILE_FILTER = block(CoreUtilitiesModBlocks.DENY_PROJECTILE_FILTER);
	public static final RegistryObject<Item> DENY_FILTER_ZOMBIE = block(CoreUtilitiesModBlocks.DENY_FILTER_ZOMBIE);
	public static final RegistryObject<Item> DENY_FILTER_SKELETON = block(CoreUtilitiesModBlocks.DENY_FILTER_SKELETON);
	public static final RegistryObject<Item> DENY_FILTER_SPIDER = block(CoreUtilitiesModBlocks.DENY_FILTER_SPIDER);
	public static final RegistryObject<Item> DENY_FILTER_VILLAGER = block(CoreUtilitiesModBlocks.DENY_FILTER_VILLAGER);
	public static final RegistryObject<Item> DENY_FILTER_PET = block(CoreUtilitiesModBlocks.DENY_FILTER_PET);
	public static final RegistryObject<Item> DENY_FILTER_ANIMAL = block(CoreUtilitiesModBlocks.DENY_FILTER_ANIMAL);
	public static final RegistryObject<Item> CONFIGURATION_WRENCH = REGISTRY.register("configuration_wrench", () -> new ConfigurationWrenchItem());
	public static final RegistryObject<Item> SMALL_BACKPACK = REGISTRY.register("small_backpack", () -> new SmallBackpackItem());
	public static final RegistryObject<Item> MEDIUM_BACKPACK = REGISTRY.register("medium_backpack", () -> new MediumBackpackItem());
	public static final RegistryObject<Item> BIG_BACKPACK = REGISTRY.register("big_backpack", () -> new BigBackpackItem());
	public static final RegistryObject<Item> MOB_NET = REGISTRY.register("mob_net", () -> new MobNetItem());
	public static final RegistryObject<Item> FLOATING_BLOCK = block(CoreUtilitiesModBlocks.FLOATING_BLOCK);
	public static final RegistryObject<Item> CABLE = block(CoreUtilitiesModBlocks.CABLE);
	public static final RegistryObject<Item> CABLE_E = block(CoreUtilitiesModBlocks.CABLE_E);
	public static final RegistryObject<Item> CABLE_I = block(CoreUtilitiesModBlocks.CABLE_I);
	public static final RegistryObject<Item> CABLE_L = block(CoreUtilitiesModBlocks.CABLE_L);
	public static final RegistryObject<Item> CABLE_LC = block(CoreUtilitiesModBlocks.CABLE_LC);
	public static final RegistryObject<Item> CABLE_LCC = block(CoreUtilitiesModBlocks.CABLE_LCC);
	public static final RegistryObject<Item> CABLE_LT = block(CoreUtilitiesModBlocks.CABLE_LT);
	public static final RegistryObject<Item> CABLE_LTC = block(CoreUtilitiesModBlocks.CABLE_LTC);
	public static final RegistryObject<Item> CABLE_T = block(CoreUtilitiesModBlocks.CABLE_T);
	public static final RegistryObject<Item> CABLE_TC = block(CoreUtilitiesModBlocks.CABLE_TC);
	public static final RegistryObject<Item> CABLE_TX = block(CoreUtilitiesModBlocks.CABLE_TX);
	public static final RegistryObject<Item> CABLE_TXC = block(CoreUtilitiesModBlocks.CABLE_TXC);
	public static final RegistryObject<Item> CABLE_TXCC = block(CoreUtilitiesModBlocks.CABLE_TXCC);
	public static final RegistryObject<Item> CABLE_X = block(CoreUtilitiesModBlocks.CABLE_X);
	public static final RegistryObject<Item> CABLE_XC = block(CoreUtilitiesModBlocks.CABLE_XC);
	public static final RegistryObject<Item> CABLE_F = block(CoreUtilitiesModBlocks.CABLE_F);
	public static final RegistryObject<Item> CABLE_S = block(CoreUtilitiesModBlocks.CABLE_S);
	public static final RegistryObject<Item> SOLAR_PANEL = block(CoreUtilitiesModBlocks.SOLAR_PANEL);
	public static final RegistryObject<Item> SOLAR_PANEL_1 = block(CoreUtilitiesModBlocks.SOLAR_PANEL_1);
	public static final RegistryObject<Item> SOLAR_PANEL_2 = block(CoreUtilitiesModBlocks.SOLAR_PANEL_2);
	public static final RegistryObject<Item> BASIC_MACHINE_FRAME = block(CoreUtilitiesModBlocks.BASIC_MACHINE_FRAME);

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return block(block, new Item.Properties());
	}

	private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}