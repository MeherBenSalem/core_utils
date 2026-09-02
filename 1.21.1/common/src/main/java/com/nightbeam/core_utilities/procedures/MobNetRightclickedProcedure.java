package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Comparator;

public class MobNetRightclickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, ItemStack itemstack) {
		var enchantment = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
		if (EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemstack) == 0) {
			Entity target = world.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(x, y, z), 4, 4, 4), e -> true).stream().sorted(new Object() {
				Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
					return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
				}
			}.compareDistOf(x, y, z)).findFirst().orElse(null);
			if (target != null && !(target instanceof TamableAnimal tamable && tamable.isTame())) {
				if (!target.level().isClientSide()) {
					target.discard();
				}
				setAnimalTag(itemstack, BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
				itemstack.enchant(enchantment, 9);
			}
		}
	}

	private static void setAnimalTag(ItemStack stack, String value) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString("animal", value));
	}
}
