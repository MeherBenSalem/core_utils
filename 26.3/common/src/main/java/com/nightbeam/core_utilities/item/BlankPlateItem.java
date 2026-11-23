package com.nightbeam.core_utilities.item;

import net.minecraft.resources.ResourceKey;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class BlankPlateItem extends Item {
	public BlankPlateItem(ResourceKey<Item> id) {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).setId(id));
	}
}