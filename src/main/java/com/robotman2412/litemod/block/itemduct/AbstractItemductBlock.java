package com.robotman2412.litemod.block.itemduct;

import com.robotman2412.litemod.block.BlockWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
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
	
	public static final VoxelShape[] SHAPES;
	
	public static List<ItemDuctPartType> PARTS = new ArrayList<>();
	
	public AbstractItemductBlock(Settings settings, String blockName, boolean doBlockItem) {
		super(settings, blockName, doBlockItem);
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
	
	public static ItemDuctPart loadPart(CompoundTag tag) {
		Identifier typeID = new Identifier(tag.getString("type"));
		for (ItemDuctPartType type : PARTS) {
			if (type.getID().equals(typeID)) {
				ItemDuctPart part = type.initialise();
				part.fromTag(tag);
				return part;
			}
		}
		new IllegalArgumentException("No such itemduct part type for " + typeID).printStackTrace();
		return null;
	}
	
	
	static {
		SHAPES = new VoxelShape[64];
		//TODO: rest of this, not really important rn
	}
	
}
