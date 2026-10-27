package com.nightbeam.core_utilities.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Blocks that add lines to their item tooltip. 26.x removed {@code Block#appendHoverText}, so
 * {@link com.nightbeam.core_utilities.item.TooltipBlockItem} forwards the call to the block.
 */
public interface BlockTooltip {
	void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag);
}
