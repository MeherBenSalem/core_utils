package com.nightbeam.core_utilities.item;

import com.nightbeam.core_utilities.conduit.ConduitBlockEntity;
import com.nightbeam.core_utilities.conduit.EnergyCableBlock;
import com.nightbeam.core_utilities.energy.EnergyHandler;
import com.nightbeam.core_utilities.machine.SolarPanelBlockEntity;
import com.nightbeam.core_utilities.platform.Services;
import com.nightbeam.core_utilities.util.Tooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

/** Reads the energy stored in any block (ours or another mod's), and shows cable network statistics. */
public class MultimeterItem extends Item {
	public MultimeterItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.core_utilities.multimeter.description").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		if (level.isClientSide || player == null) {
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		BlockPos pos = context.getClickedPos();
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof ConduitBlockEntity conduit) {
			var network = conduit.getNetwork();
			Component rate = level.getBlockState(pos).getBlock() instanceof EnergyCableBlock cable
					? Component.translatable("message.core_utilities.multimeter.rate", Tooltips.formatEnergy(cable.getTier().transferRate()))
					: Component.empty();
			player.displayClientMessage(Component.translatable("message.core_utilities.multimeter.network", network.members().size(), network.endpoints().size()).append(" ").append(rate).withStyle(ChatFormatting.AQUA), true);
			return InteractionResult.SUCCESS;
		}
		EnergyHandler handler = Services.ENERGY.find(level, pos, context.getClickedFace());
		if (handler == null) {
			handler = Services.ENERGY.find(level, pos, null);
		}
		if (handler == null) {
			player.displayClientMessage(Component.translatable("message.core_utilities.multimeter.none").withStyle(ChatFormatting.RED), true);
			return InteractionResult.SUCCESS;
		}
		Component message = Tooltips.energy(handler.getEnergyStored(), handler.getMaxEnergyStored());
		if (entity instanceof SolarPanelBlockEntity solar) {
			message = message.copy().append(Component.literal("  ")).append(Tooltips.rate("tooltip.core_utilities.generating", solar.getLastGeneration()));
		}
		player.displayClientMessage(message, true);
		return InteractionResult.SUCCESS;
	}
}
