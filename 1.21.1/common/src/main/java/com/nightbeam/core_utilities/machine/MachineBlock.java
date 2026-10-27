package com.nightbeam.core_utilities.machine;

import com.nightbeam.core_utilities.util.Tooltips;
import com.nightbeam.core_utilities.util.Wrenchable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/** Horizontal facing machine block with an "active" state that lights up the front face. */
public class MachineBlock extends Block implements EntityBlock, Wrenchable {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	private final Supplier<? extends BlockEntityType<? extends MachineBlockEntity>> type;
	private final BiFunction<BlockPos, BlockState, ? extends MachineBlockEntity> factory;
	private final String tooltipKey;

	public MachineBlock(Supplier<? extends BlockEntityType<? extends MachineBlockEntity>> type, BiFunction<BlockPos, BlockState, ? extends MachineBlockEntity> factory, String tooltipKey) {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.0f, 6.0f).requiresCorrectToolForDrops()
				.lightLevel(state -> state.getValue(ACTIVE) ? 10 : 0));
		this.type = type;
		this.factory = factory;
		this.tooltipKey = tooltipKey;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ACTIVE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return factory.apply(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (level.isClientSide || blockEntityType != type.get()) {
			return null;
		}
		return (lvl, pos, st, be) -> ((MachineBlockEntity) be).serverTick();
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof MachineBlockEntity machine) {
			player.openMenu(machine);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public boolean onWrench(Level level, BlockPos pos, BlockState state, Player player, BlockHitResult hit) {
		if (!level.isClientSide) {
			level.setBlock(pos, state.setValue(FACING, state.getValue(FACING).getClockWise()), 3);
		}
		return true;
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof MachineBlockEntity machine) {
				Containers.dropContents(level, pos, machine.getDrops());
				level.updateNeighbourForOutputSignal(pos, this);
			}
		}
		super.onRemove(state, level, pos, newState, moving);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof MachineBlockEntity machine && machine.getEnergy().getMaxEnergyStored() > 0) {
			return (int) Math.floor(15.0 * machine.getEnergy().getEnergyStored() / machine.getEnergy().getMaxEnergyStored());
		}
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
	}
}
