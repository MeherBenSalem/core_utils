package com.nightbeam.core_utilities.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.List;

public class FloatingBlockBlock extends Block {
	public FloatingBlockBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1f, 10f));
	}


}