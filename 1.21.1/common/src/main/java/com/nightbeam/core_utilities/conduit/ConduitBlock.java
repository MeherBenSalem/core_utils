package com.nightbeam.core_utilities.conduit;

import com.nightbeam.core_utilities.util.Wrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared block logic for cables and pipes: six connection properties, a shape that follows them and
 * wrench configuration of single faces.
 */
public abstract class ConduitBlock extends Block implements EntityBlock, Wrenchable {
	private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
	private static final Map<Direction, VoxelShape> ARMS = new HashMap<>();
	private final Map<BlockState, VoxelShape> shapeCache = new HashMap<>();

	static {
		ARMS.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5));
		ARMS.put(Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16));
		ARMS.put(Direction.WEST, Block.box(0, 5, 5, 5, 11, 11));
		ARMS.put(Direction.EAST, Block.box(11, 5, 5, 16, 11, 11));
		ARMS.put(Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11));
		ARMS.put(Direction.UP, Block.box(5, 11, 5, 11, 16, 11));
	}

	protected ConduitBlock(Properties properties) {
		super(properties);
	}

	public abstract EnumProperty<ConnectionType> property(Direction direction);

	/** Whether the block on the other side can be connected to as an endpoint (energy storage, inventory...). */
	protected abstract boolean canConnectTo(Level level, BlockPos neighbor, Direction neighborFace);

	protected boolean allowsExtraction() {
		return false;
	}

	protected BlockState defaultConnections(BlockState state) {
		for (Direction direction : Direction.values()) {
			state = state.setValue(property(direction), ConnectionType.NONE);
		}
		return state;
	}

	public ConnectionType computeConnection(LevelAccessor level, BlockPos pos, Direction direction, ConnectionType current) {
		if (level.getBlockEntity(pos) instanceof ConduitBlockEntity self && self.isFaceDisabled(direction)) {
			return ConnectionType.NONE;
		}
		BlockPos neighborPos = pos.relative(direction);
		BlockState neighbor = level.getBlockState(neighborPos);
		if (neighbor.getBlock() == this) {
			if (level.getBlockEntity(neighborPos) instanceof ConduitBlockEntity other && other.isFaceDisabled(direction.getOpposite())) {
				return ConnectionType.NONE;
			}
			return ConnectionType.CONDUIT;
		}
		if (neighbor.getBlock() instanceof ConduitBlock) {
			return ConnectionType.NONE;
		}
		if (level instanceof Level realLevel && canConnectTo(realLevel, neighborPos, direction.getOpposite())) {
			return current == ConnectionType.EXTRACT && allowsExtraction() ? ConnectionType.EXTRACT : ConnectionType.ENDPOINT;
		}
		return ConnectionType.NONE;
	}

	public BlockState refreshConnections(LevelAccessor level, BlockPos pos, BlockState state) {
		for (Direction direction : Direction.values()) {
			state = state.setValue(property(direction), computeConnection(level, pos, direction, state.getValue(property(direction))));
		}
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return refreshConnections(context.getLevel(), context.getClickedPos(), defaultConnections(defaultBlockState()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		ConnectionType current = state.getValue(property(direction));
		ConnectionType updated = computeConnection(level, pos, direction, current);
		if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
			conduit.invalidateNetwork();
		}
		return updated == current ? state : state.setValue(property(direction), updated);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
		super.neighborChanged(state, level, pos, block, fromPos, moving);
		if (level.isClientSide) {
			return;
		}
		BlockState refreshed = refreshConnections(level, pos, state);
		if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
			conduit.invalidateNetwork();
		}
		if (refreshed != state) {
			level.setBlock(pos, refreshed, Block.UPDATE_CLIENTS);
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(state, level, pos, oldState, moving);
		invalidateAround(level, pos);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		if (!state.is(newState.getBlock())) {
			invalidateAround(level, pos);
		}
		super.onRemove(state, level, pos, newState, moving);
	}

	protected void invalidateAround(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ConduitBlockEntity self) {
			self.invalidateNetwork();
		}
		for (Direction direction : Direction.values()) {
			if (level.getBlockEntity(pos.relative(direction)) instanceof ConduitBlockEntity other) {
				other.invalidateNetwork();
			}
		}
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapeCache.computeIfAbsent(state, s -> {
			VoxelShape shape = CORE;
			for (Direction direction : Direction.values()) {
				if (s.getValue(property(direction)).isConnected()) {
					shape = Shapes.or(shape, ARMS.get(direction));
				}
			}
			return shape;
		});
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	/** Which face of the conduit was clicked, taking the arms into account. */
	public static Direction clickedFace(BlockPos pos, BlockHitResult hit) {
		Vec3 local = hit.getLocation().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		double ax = Math.abs(local.x), ay = Math.abs(local.y), az = Math.abs(local.z);
		double max = Math.max(ax, Math.max(ay, az));
		if (max < 0.2) {
			return hit.getDirection();
		}
		if (max == ax) {
			return local.x > 0 ? Direction.EAST : Direction.WEST;
		}
		if (max == ay) {
			return local.y > 0 ? Direction.UP : Direction.DOWN;
		}
		return local.z > 0 ? Direction.SOUTH : Direction.NORTH;
	}

	@Override
	public boolean onWrench(Level level, BlockPos pos, BlockState state, Player player, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit)) {
			return false;
		}
		Direction face = clickedFace(pos, hit);
		ConnectionType current = state.getValue(property(face));
		BlockState updated;
		String message;
		if (conduit.isFaceDisabled(face)) {
			conduit.setFaceDisabled(face, false);
			updated = state.setValue(property(face), computeConnection(level, pos, face, ConnectionType.NONE));
			message = "connected";
		} else if (current == ConnectionType.ENDPOINT && allowsExtraction()) {
			updated = state.setValue(property(face), ConnectionType.EXTRACT);
			message = "extract";
		} else {
			conduit.setFaceDisabled(face, true);
			updated = state.setValue(property(face), ConnectionType.NONE);
			message = "disconnected";
		}
		if (!level.isClientSide) {
			level.setBlock(pos, updated, Block.UPDATE_ALL);
			BlockPos other = pos.relative(face);
			BlockState otherState = level.getBlockState(other);
			if (otherState.getBlock() == this) {
				level.setBlock(other, refreshConnections(level, other, otherState), Block.UPDATE_ALL);
			}
			invalidateAround(level, pos);
			player.displayClientMessage(Component.translatable("message.core_utilities.conduit." + message, Component.translatable("direction.core_utilities." + face.getName())), true);
		}
		return true;
	}
}
