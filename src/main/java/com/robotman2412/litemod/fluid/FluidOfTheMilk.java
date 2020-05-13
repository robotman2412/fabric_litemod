package com.robotman2412.litemod.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class FluidOfTheMilk extends BaseFluid {
	
	@Override
	public Fluid getFlowing() {
		return null;
	}
	
	@Override
	public Fluid getStill() {
		return null;
	}
	
	@Override
	protected boolean isInfinite() {
		return false;
	}
	
	@Override
	protected void beforeBreakingBlock(IWorld world, BlockPos pos, BlockState state) {
		
	}
	
	@Override
	protected int method_15733(WorldView worldView) {
		return 0;
	}
	
	@Override
	protected int getLevelDecreasePerBlock(WorldView world) {
		return 1;
	}
	
	@Override
	public Item getBucketItem() {
		return Items.MILK_BUCKET;
	}
	
	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}
	
	@Override
	public int getTickRate(WorldView world) {
		return 4;
	}
	
	@Override
	protected float getBlastResistance() {
		return 100;
	}
	
	@Override
	protected BlockState toBlockState(FluidState state) {
		return null;
	}
	
	@Override
	public boolean isStill(FluidState state) {
		return false;
	}
	
	@Override
	public int getLevel(FluidState state) {
		return 0;
	}
	
}
