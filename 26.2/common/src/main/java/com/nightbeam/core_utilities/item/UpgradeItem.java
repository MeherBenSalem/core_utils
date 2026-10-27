package com.nightbeam.core_utilities.item;

import net.minecraft.resources.ResourceKey;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class UpgradeItem extends Item {
	public enum Type {
		SPEED,
		ENERGY,
		RANGE
	}

	private final Type type;

	public UpgradeItem(ResourceKey<Item> id, Type type) {
		super(new Item.Properties().stacksTo(8).setId(id));
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		tooltipAdder.accept(Component.translatable("tooltip.core_utilities.upgrade." + type.name().toLowerCase(java.util.Locale.ROOT)).withStyle(ChatFormatting.GRAY));
		tooltipAdder.accept(Component.translatable("tooltip.core_utilities.upgrade.stack").withStyle(ChatFormatting.DARK_GRAY));
	}
}
