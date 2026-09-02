package com.nightbeam.core_utilities.item;

import com.nightbeam.core_utilities.platform.Services;
import com.nightbeam.core_utilities.world.inventory.MediumBackpackGUIMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class MediumBackpackItem extends Item {
	public MediumBackpackItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.core_utilities.medium_backpack.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		if (entity instanceof ServerPlayer serverPlayer) {
			Services.PLATFORM.openMenu(serverPlayer, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("Medium Backpack");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					FriendlyByteBuf packetBuffer = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
					packetBuffer.writeBlockPos(entity.blockPosition());
					packetBuffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
					return new MediumBackpackGUIMenu(id, inventory, packetBuffer);
				}
			}, buf -> {
				buf.writeBlockPos(entity.blockPosition());
				buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
			});
		}
		return ar;
	}
}
