package com.nightbeam.core_utilities.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeItem extends Item {
	public enum Type {
		SPEED,
		ENERGY,
		RANGE
	}

	private final Type type;

	public UpgradeItem(Type type) {
		super(new Item.Properties().stacksTo(8));
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.core_utilities.upgrade." + type.name().toLowerCase(java.util.Locale.ROOT)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("tooltip.core_utilities.upgrade.stack").withStyle(ChatFormatting.DARK_GRAY));
	}
}
