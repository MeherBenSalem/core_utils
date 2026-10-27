package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.util.Tooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/** Tiered solar panel. The weather property only switches the top texture. */
public class SolarPanelBlock extends Block implements EntityBlock {
	public static final IntegerProperty WEATHER = IntegerProperty.create("weather", 0, 2);
	private final int tier;
	private final Supplier<? extends BlockEntityType<SolarPanelBlockEntity>> type;

	public SolarPanelBlock(int tier, Supplier<? extends BlockEntityType<SolarPanelBlockEntity>> type) {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1.5f, 6f).requiresCorrectToolForDrops());
		this.tier = tier;
		this.type = type;
		registerDefaultState(stateDefinition.any().setValue(WEATHER, 0));
	}

	public int getTier() {
		return tier;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WEATHER);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SolarPanelBlockEntity(type.get(), pos, state, tier);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (level.isClientSide) {
			return null;
		}
		return (lvl, pos, st, be) -> {
			if (be instanceof SolarPanelBlockEntity solar) {
				solar.serverTick();
			}
		};
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Tooltips.rate("tooltip.core_utilities.generates", SolarPanelBlockEntity.rate(tier)));
		tooltip.add(Component.translatable("tooltip.core_utilities.solar_panel").withStyle(ChatFormatting.GRAY));
	}
}
