package com.nightbeam.core_utilities.procedures;

import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;

public class SpiderFilterProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof Spider)) {
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