package com.robotman2412.litemod.mixinutil;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class PistonUtils {
	
	public static double pistonPushLimit = 12;
	
	public static double getWeightForBlock(BlockView world, BlockPos pos) {
		try {
			BlockState state = world.getBlockState(pos);
			if (state instanceof IWeightedBlock) {
				return ((IWeightedBlock) state).getWeight(world, pos, state);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	public static boolean isBlockSticky(Block block) {
		return block instanceof IStickyBlock || block instanceof SlimeBlock || block instanceof HoneyBlock;
	}
	
	public static boolean willBlocksStick(Block block, Block block2) {
		if (block instanceof IStickyBlock) {
			if (block2 instanceof IStickyBlock) {
				return ((IStickyBlock) block).willStickToOther(block2) &&
						((IStickyBlock) block2).willStickToOther(block);
			}
			else
			{
				return ((IStickyBlock) block).willStickToOther(block2);
			}
		}
		else if (block2 instanceof IStickyBlock) {
			return ((IStickyBlock) block2).willStickToOther(block);
		}
		else if (block == Blocks.HONEY_BLOCK && block2 == Blocks.SLIME_BLOCK) {
			return false;
		} else if (block == Blocks.SLIME_BLOCK && block2 == Blocks.HONEY_BLOCK) {
			return false;
		} else {
			return isBlockSticky(block) || isBlockSticky(block2);
		}
	}
	
}
