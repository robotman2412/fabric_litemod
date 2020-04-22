package com.robotman2412.litemod.block.pushableredstone;

import com.robotman2412.litemod.mixinutil.IWeightedBlock;
import net.minecraft.block.*;
import net.minecraft.entity.EntityContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class PushaPeater extends RepeaterBlock implements IWeightedBlock {
	
	public static final VoxelShape PUSHABLE_REDSTONE_COMPONENT_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	
	public PushaPeater() {
		super(Settings.of(Material.WOOD));
	}
	
	@Override
	public BlockSoundGroup getSoundGroup(BlockState state) {
		return BlockSoundGroup.WOOD;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return PUSHABLE_REDSTONE_COMPONENT_SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return AbstractRedstoneGateBlock.SHAPE;
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return true;
	}
	
	@Override
	public double getWeight(BlockView view, BlockPos pos, BlockState state) {
		return 0.5;
	}
	
}
