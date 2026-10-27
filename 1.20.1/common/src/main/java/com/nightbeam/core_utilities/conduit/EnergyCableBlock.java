package com.nightbeam.core_utilities.conduit;

import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import com.nightbeam.core_utilities.platform.Services;
import com.nightbeam.core_utilities.util.Tooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EnergyCableBlock extends ConduitBlock {
	public static final Map<Direction, EnumProperty<ConnectionType>> PROPERTIES = new EnumMap<>(Direction.class);

	static {
		for (Direction direction : Direction.values()) {
			PROPERTIES.put(direction, EnumProperty.create(direction.getName(), ConnectionType.class, ConnectionType.NONE, ConnectionType.CONDUIT, ConnectionType.ENDPOINT));
		}
	}

	private final CableTier tier;

	public EnergyCableBlock(CableTier tier) {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).strength(0.5f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.tier = tier;
		registerDefaultState(defaultConnections(stateDefinition.any()));
	}

	public CableTier getTier() {
		return tier;
	}

	@Override
	public EnumProperty<ConnectionType> property(Direction direction) {
		return PROPERTIES.get(direction);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		PROPERTIES.values().forEach(builder::add);
	}

	@Override
	protected boolean canConnectTo(Level level, BlockPos neighbor, Direction neighborFace) {
		return Services.ENERGY.find(level, neighbor, neighborFace) != null;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EnergyCableBlockEntity(CoreUtilitiesModBlockEntities.CABLE.get(), pos, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Tooltips.rate("tooltip.core_utilities.cable_rate", tier.transferRate()));
		tooltip.add(Component.translatable("tooltip.core_utilities.cable_hint").withStyle(net.minecraft.ChatFormatting.GRAY));
	}
}
