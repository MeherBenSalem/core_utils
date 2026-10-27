package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Damages mobs in a box in front of it and vacuums their drops into its inventory. Players, named mobs and
 * bosses are never touched.
 */
public class MobGrinderBlockEntity extends AreaMachineBlockEntity {
	public MobGrinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, MachineSpec spec) {
		super(type, pos, state, spec, 60_000);
	}

	public AABB area() {
		int radius = radius(2, 8);
		Direction facing = getFacing();
		BlockPos center = worldPosition.relative(facing, radius + 1);
		return new AABB(center).inflate(radius, 1, radius).expandTowards(0, 1, 0);
	}

	@Override
	protected boolean work() {
		maxProgress = scaledTime(20);
		progress++;
		if (progress < maxProgress) {
			return progress > 1 || energy.getEnergyStored() > 0;
		}
		progress = 0;
		AABB area = area();
		boolean worked = false;
		int usage = scaledUsage(CoreUtilitiesConfig.get().mobGrinderEnergyPerHit);
		List<Mob> mobs = level.getEntitiesOfClass(Mob.class, area, mob -> mob.isAlive() && !mob.hasCustomName() && mob.canChangeDimensions() && !mob.isInvulnerable());
		for (LivingEntity mob : mobs) {
			if (!energy.consume(usage)) {
				break;
			}
			lastUsage += usage;
			mob.hurt(level.damageSources().generic(), CoreUtilitiesConfig.get().mobGrinderDamage);
			worked = true;
		}
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area.inflate(1), ItemEntity::isAlive)) {
			ItemStack rest = output(item.getItem().copy(), spec.indices(SlotKind.OUTPUT));
			if (rest.isEmpty()) {
				item.discard();
			} else {
				item.setItem(rest);
			}
			worked = true;
		}
		if (worked) {
			setChanged();
		}
		return worked;
	}
}
