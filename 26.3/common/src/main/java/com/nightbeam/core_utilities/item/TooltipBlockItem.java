package com.nightbeam.core_utilities.item;

import com.nightbeam.core_utilities.util.BlockTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Block item that shows the tooltip lines of {@link BlockTooltip} blocks. */
public class TooltipBlockItem extends BlockItem {
	public TooltipBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltipAdder, flag);
		if (getBlock() instanceof BlockTooltip tooltip) {
			List<Component> lines = new ArrayList<>();
			tooltip.appendHoverText(stack, context, lines, flag);
			lines.forEach(tooltipAdder);
		}
	}
}
