package com.nightbeam.core_utilities.conduit;

import com.nightbeam.core_utilities.config.CoreUtilitiesConfig;
import com.nightbeam.core_utilities.init.CoreUtilitiesModBlockEntities;
import com.nightbeam.core_utilities.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ItemPipeBlock extends ConduitBlock {
	public static final Map<Direction, EnumProperty<ConnectionType>> PROPERTIES = new EnumMap<>(Direction.class);

	static {
		for (Direction direction : Direction.values()) {
			PROPERTIES.put(direction, EnumProperty.create(direction.getName(), ConnectionType.class));
		}
	}

	private final boolean rapid;

	public ItemPipeBlock(boolean rapid) {
		super(BlockBehaviour.Properties.of().mapColor(rapid ? MapColor.GOLD : MapColor.METAL).sound(SoundType.METAL).strength(0.5f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.rapid = rapid;
		registerDefaultState(defaultConnections(stateDefinition.any()));
	}

	public int itemsPerOperation() {
		return rapid ? CoreUtilitiesConfig.get().itemPipeRapidItems : CoreUtilitiesConfig.get().itemPipeBasicItems;
	}

	public int interval() {
		return Math.max(1, rapid ? CoreUtilitiesConfig.get().itemPipeRapidInterval : CoreUtilitiesConfig.get().itemPipeBasicInterval);
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
	protected boolean allowsExtraction() {
		return true;
	}

	@Override
	protected boolean canConnectTo(Level level, BlockPos neighbor, Direction neighborFace) {
		return Services.ITEMS.find(level, neighbor, neighborFace) != null;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemPipeBlockEntity(CoreUtilitiesModBlockEntities.ITEM_PIPE.get(), pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) {
			return null;
		}
		return (lvl, pos, st, be) -> {
			if (be instanceof ItemPipeBlockEntity pipe) {
				pipe.serverTick();
			}
		};
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.core_utilities.pipe_rate", itemsPerOperation(), String.format(java.util.Locale.ROOT, "%.2f", interval() / 20.0)).withStyle(ChatFormatting.AQUA));
		tooltip.add(Component.translatable("tooltip.core_utilities.pipe_hint").withStyle(ChatFormatting.GRAY));
	}
}
