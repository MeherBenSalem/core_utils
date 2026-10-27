package com.nightbeam.core_utilities.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/** Opens the in-game manual. The screen class is only touched on the client. */
public class GuideBookItem extends Item {
	public GuideBookItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide) {
			com.nightbeam.core_utilities.client.ClientHooks.openGuide();
		}
		return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.core_utilities.guide_book.description").withStyle(ChatFormatting.GRAY));
	}
}
