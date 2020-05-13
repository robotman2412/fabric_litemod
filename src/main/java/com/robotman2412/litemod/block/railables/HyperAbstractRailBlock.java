package com.robotman2412.litemod.block.railables;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class HyperAbstractRailBlock extends AbstractRailBlock {
	
	protected HyperAbstractRailBlock(boolean allowCurves, Settings settings) {
		super(allowCurves, settings);
	}
	
	public abstract RailShape getRailShapeFor(World world, AbstractMinecartEntity minecart, BlockPos pos, BlockState state);
	
}
