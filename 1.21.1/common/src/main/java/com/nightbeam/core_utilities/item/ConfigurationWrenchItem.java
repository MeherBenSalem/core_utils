package com.nightbeam.core_utilities.item;

import com.nightbeam.core_utilities.CoreUtilitiesMod;
import com.nightbeam.core_utilities.util.Wrenchable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * Right-click: configure (rotate machines, connect/disconnect/extract conduit faces).
 * Sneak + right-click: dismantle any Core Utilities block into its item, keeping its contents.
 */
public class ConfigurationWrenchItem extends Item {
	public ConfigurationWrenchItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.core_utilities.configuration_wrench.description_0").withStyle(ChatFormatting.GRAY));
		list.add(Component.translatable("item.core_utilities.configuration_wrench.description_1").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = context.getPlayer();
		boolean ours = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals(CoreUtilitiesMod.MOD_ID);
		if (player != null && player.isSecondaryUseActive() && ours) {
			if (!level.isClientSide) {
				Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, context.getItemInHand());
				level.destroyBlock(pos, false, player);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		if (player != null && state.getBlock() instanceof Wrenchable wrenchable) {
			BlockHitResult hit = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, context.isInside());
			if (wrenchable.onWrench(level, pos, state, player, hit)) {
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
		return InteractionResult.PASS;
	}
}
