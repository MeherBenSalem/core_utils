package com.nightbeam.core_utilities.procedures;

import com.nightbeam.core_utilities.util.TagHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class ConfigurationWrenchRightclickedOnBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		if (TagHelper.isInTag(blockstate, "core_utilities:cables/f/copper")) {
			{
				BlockPos _pos = BlockPos.containing(x, y, z);
				Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
				world.destroyBlock(_pos, false);
			}
		}
	}
}
