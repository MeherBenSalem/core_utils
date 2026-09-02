package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;

public class DenyZombieFilterProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof Zombie) || (entity instanceof ZombieHorse) || (entity instanceof Zoglin)) {
			if ((entity.getDirection()) == Direction.NORTH) {
				entity.push(0, 0, 1);
			} else if ((entity.getDirection()) == Direction.EAST) {
				entity.push((-1), 0, 0);
			} else if ((entity.getDirection()) == Direction.SOUTH) {
				entity.push(0, 0, (-1));
			} else if ((entity.getDirection()) == Direction.UP) {
				entity.push(0, 1, 0);
			} else if ((entity.getDirection()) == Direction.DOWN) {
				entity.push(0, (-1), 0);
			} else {
				entity.push(1, 0, 0);
			}
		}
	}
}
