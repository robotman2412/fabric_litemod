package com.robotman2412.litemod.block.pushableredstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class PushaRedstoneTorch extends RedstoneTorchBlock {
	
	protected static final VoxelShape BOUNDING_SHAPE = Block.createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 11.0D, 11.0D);
	
	public PushaRedstoneTorch() {
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
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return BOUNDING_SHAPE;
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
