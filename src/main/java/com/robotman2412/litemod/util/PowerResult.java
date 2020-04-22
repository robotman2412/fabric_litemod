package com.robotman2412.litemod.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PowerResult {
	
	public int powerLevel;
	public boolean isStronglyPowered;
	BlockPos[] powerContributors;
	
	public PowerResult(int powerLevel, boolean isStronglyPowered, BlockPos... powerContributors) {
		this.powerLevel = powerLevel;
		this.isStronglyPowered = isStronglyPowered;
		this.powerContributors = powerContributors;
	}
	
	public static PowerResult getInducedPower(World worldIn, BlockPos pos) {
		List<BlockPos> powerContributors = new ArrayList<>();
		PowerResult res = new PowerResult(0, false);
		getInducedPower0(worldIn, res, powerContributors, Direction.DOWN, pos.add(0, 1, 0));
		getInducedPower0(worldIn, res, powerContributors, Direction.UP, pos.add(0, -1, 0));
		getInducedPower0(worldIn, res, powerContributors, Direction.EAST, pos.add(-1, 0, 0));
		getInducedPower0(worldIn, res, powerContributors, Direction.WEST, pos.add(1, 0, 0));
		getInducedPower0(worldIn, res, powerContributors, Direction.NORTH, pos.add(0, 0, 1));
		getInducedPower0(worldIn, res, powerContributors, Direction.SOUTH, pos.add(0, 0, -1));
		res.powerContributors = powerContributors.toArray(new BlockPos[0]);
		return res;
	}
	
	public static void getInducedPower0(World worldIn, PowerResult power, List<BlockPos> powerContributors, Direction directionToPoweredBlock, BlockPos powererPos) {
		if (powererPos.getY() < 0 || powererPos.getY() > 255) {
			return;
		}
		BlockState state = worldIn.getBlockState(powererPos);
		int weak = state.getWeakRedstonePower(worldIn, powererPos, directionToPoweredBlock.getOpposite());
		int strong = state.getStrongRedstonePower(worldIn, powererPos, directionToPoweredBlock.getOpposite());
		if (state.getBlock() == Blocks.REDSTONE_WIRE) {
			strong = 0;
		}
		//System.out.printf("%s: %s, %d weak, %d strong\n", directionToPoweredBlock.getOpposite().toString(), state.toString(), weak, strong);
		if (strong > 0) {
			power.isStronglyPowered = true;
			power.powerLevel = Math.max(power.powerLevel, strong);
			powerContributors.add(powererPos);
		}
		else if (weak > 0) {
			power.powerLevel = Math.max(power.powerLevel, weak);
			powerContributors.add(powererPos);
		}
	}
	
}
