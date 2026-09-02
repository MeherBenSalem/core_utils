package com.nightbeam.core_utilities.item;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

import com.nightbeam.core_utilities.procedures.MobNetRightclickedProcedure;
import com.nightbeam.core_utilities.procedures.MobNetRightclickedOnBlockProcedure;

public class MobNetItem extends Item {
	public MobNetItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, tooltipDisplay, tooltipAdder, flag);
		tooltipAdder.accept(Component.translatable("item.core_utilities.mob_net.description_0"));
		tooltipAdder.accept(Component.translatable("item.core_utilities.mob_net.description_1"));
	}

	@Override
	public InteractionResult use(Level world, Player entity, InteractionHand hand) {
		ItemStack stack = entity.getItemInHand(hand);
		MobNetRightclickedProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), stack);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		MobNetRightclickedOnBlockProcedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getItemInHand());
		return InteractionResult.SUCCESS;
	}
}