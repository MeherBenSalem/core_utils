package com.nightbeam.core_utilities.item;

import net.minecraft.resources.ResourceKey;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** Opens the in-game manual. The screen class is only touched on the client. */
public class GuideBookItem extends Item {
	public GuideBookItem(ResourceKey<Item> id) {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).setId(id));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			com.nightbeam.core_utilities.client.ClientHooks.openGuide();
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		tooltipAdder.accept(Component.translatable("item.core_utilities.guide_book.description").withStyle(ChatFormatting.GRAY));
	}
}
