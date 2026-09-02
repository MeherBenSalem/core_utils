package com.nightbeam.core_utilities.util;

import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.world.level.block.state.BlockState;

public final class TagHelper {
	private TagHelper() {
	}

	public static boolean isInTag(BlockState state, String tagPath) {
		return Services.PLATFORM.isBlockInTag(state, tagPath);
	}
}
