package com.robotman2412.litemod.mixinutil;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface IWeightedBlock {
	
	double getWeight(BlockView view, BlockPos pos, BlockState state);
	
}
