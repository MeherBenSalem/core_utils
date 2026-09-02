package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class MobNetRightclickedOnBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, ItemStack itemstack) {
		var enchantment = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
		if (EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemstack) != 0) {
			if (world instanceof ServerLevel level) {
				level.getServer().getCommands().performPrefixedCommand(
						new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null).withSuppressedOutput(),
						"summon " + getAnimalTag(itemstack) + " ~ ~ ~");
			}
			itemstack.remove(DataComponents.ENCHANTMENTS);
			setAnimalTag(itemstack, "");
		}
	}

	private static String getAnimalTag(ItemStack stack) {
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		return data.copyTag().getString("animal");
	}

	private static void setAnimalTag(ItemStack stack, String value) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString("animal", value));
	}
}
