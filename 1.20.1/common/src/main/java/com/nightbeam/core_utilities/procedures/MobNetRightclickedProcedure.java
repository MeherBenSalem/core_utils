package com.nightbeam.core_utilities.procedures;


import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Comparator;

public class MobNetRightclickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, ItemStack itemstack) {
		Entity target = null;
		if (!(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, itemstack) != 0)) {
			target = (Entity) world.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(x, y, z), 4, 4, 4), e -> true).stream().sorted(new Object() {
				Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
					return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
				}
			}.compareDistOf(x, y, z)).findFirst().orElse(null);
			if (!(target == null)) {
				if (!(target instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false)) {
					if (!target.level().isClientSide())
						target.discard();
					itemstack.getOrCreateTag().putString("animal", BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
					itemstack.enchant(Enchantments.UNBREAKING, 9);
				}
			}
		}
	}
}