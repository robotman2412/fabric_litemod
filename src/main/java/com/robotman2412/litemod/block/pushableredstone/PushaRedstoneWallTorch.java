package com.robotman2412.litemod.block.pushableredstone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

import java.util.Map;

public class PushaRedstoneWallTorch extends WallRedstoneTorchBlock {
	
	//long line cancer
	private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, Block.createCuboidShape(5D, 3.0D, 10.0D, 11D, 14.0D, 16.0D),
			Direction.SOUTH, Block.createCuboidShape(5D, 3.0D, 0.0D, 11D, 14.0D, 6.0D),
			Direction.WEST, Block.createCuboidShape(10.0D, 3.0D, 5D, 16.0D, 14.0D, 11D),
			Direction.EAST, Block.createCuboidShape(0.0D, 3.0D, 5D, 6.0D, 14.0D, 11D)
	));
	
	public PushaRedstoneWallTorch() {
		super(Settings.of(Material.WOOD));
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean hasEmissiveLighting(BlockState state) {
		return state.get(LIT);
	}
	
	@Override
	public int getLuminance(BlockState state) {
		return state.get(LIT) ? 6 : 0;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView view, BlockPos pos, EntityContext context) {
		return BOUNDING_SHAPES.get(state.get(FACING));
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return super.getOutlineShape(state, view, pos, context);
	}
	
	@Override
	public BlockSoundGroup getSoundGroup(BlockState state) {
		return BlockSoundGroup.WOOD;
	}
	
}
