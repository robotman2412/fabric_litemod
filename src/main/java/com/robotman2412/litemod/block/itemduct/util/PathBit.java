package com.robotman2412.litemod.block.itemduct.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PathBit {
	
	public BlockPos pos;
	public BlockEntity blockEntity;
	public Direction directionFromPrevious;
	public PathBit previous;
	public int cost;
	
	public PathBit(BlockPos pos, BlockEntity blockEntity, Direction fromPrevious, PathBit previous) {
		this.pos = pos;
		this.blockEntity = blockEntity;
		this.directionFromPrevious = fromPrevious;
		this.previous = previous;
	}
	
}
