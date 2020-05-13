package com.robotman2412.litemod.block.itemduct;

import com.robotman2412.litemod.block.BlockWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractItemductBlock extends BlockWrapper implements BlockEntityProvider {
	
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty DOWN = BooleanProperty.of("down");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	
	public static BooleanProperty getPropertyForDirection(Direction direction) {
		switch (direction) {
			default:
				throw new NullPointerException("Input direction is null!");
			case DOWN:
				return DOWN;
			case UP:
				return UP;
			case NORTH:
				return NORTH;
			case EAST:
				return EAST;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
		}
	}
	
	public static final VoxelShape[] SHAPES;
	
	public static List<ItemDuctPartType> PARTS = new ArrayList<>();
	
	public AbstractItemductBlock(Settings settings, String blockName, boolean doBlockItem) {
		super(settings, blockName, doBlockItem);
		setDefaultState(getDefaultState()
				.with(UP, false)
				.with(DOWN, false)
				.with(NORTH, false)
				.with(EAST, false)
				.with(SOUTH, false)
				.with(WEST, false)
		);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView view, BlockPos pos) {
		return 1;
	}
	
	@Override
	public boolean hasBlockEntity() {
		return true;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		int i = (state.get(UP)    ? 1 : 0) |
				(state.get(DOWN)  ? 2 : 0) |
				(state.get(NORTH) ? 4 : 0) |
				(state.get(EAST)  ? 8 : 0) |
				(state.get(SOUTH) ? 16 : 0) |
				(state.get(WEST)  ? 32 : 0);
		return SHAPES[i];
	}
	
	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.getTranslucent();
	}
	
	public static ItemDuctPart loadPart(CompoundTag tag, AbstractItemductBlockEntity entity, Direction direction) {
		Identifier typeID = new Identifier(tag.getString("type"));
		for (ItemDuctPartType type : PARTS) {
			if (type.getID().equals(typeID)) {
				ItemDuctPart part = type.initialise(entity, entity.getPos(), direction);
				part.fromTag(tag);
				return part;
			}
		}
		new IllegalArgumentException("No such itemduct part type for " + typeID).printStackTrace();
		return null;
	}
	
	
	static {
		SHAPES = new VoxelShape[64];
		VoxelShape centerShape = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
		VoxelShape northShape = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
		VoxelShape southShape = Block.createCuboidShape(5, 5, 11, 11, 11, 16);
		VoxelShape eastShape = Block.createCuboidShape(11, 5, 5, 16, 11, 11);
		VoxelShape westShape = Block.createCuboidShape(0, 5, 5, 5, 11, 11);
		VoxelShape upShape = Block.createCuboidShape(5, 11, 5, 11, 16, 11);
		VoxelShape downShape = Block.createCuboidShape(5, 0, 5, 11, 5, 11);
		for (int i = 0; i < 64; i++) {
			VoxelShape shape = centerShape;
			if ((i & 1) > 0) { //up
				shape = VoxelShapes.union(shape, upShape);
			}
			if ((i & 2) > 0) { //down
				shape = VoxelShapes.union(shape, downShape);
			}
			if ((i & 4) > 0) { //north
				shape = VoxelShapes.union(shape, northShape);
			}
			if ((i & 8) > 0) { //east
				shape = VoxelShapes.union(shape, eastShape);
			}
			if ((i & 16) > 0) { //south
				shape = VoxelShapes.union(shape, southShape);
			}
			if ((i & 32) > 0) { //west
				shape = VoxelShapes.union(shape, westShape);
			}
			SHAPES[i] = shape;
		}
	}
	
}
