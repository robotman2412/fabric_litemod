package com.robotman2412.litemod.block.itemduct.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

@FunctionalInterface
public interface ItemductPredicate {
	
	boolean test(BlockEntity targetBlockEntity, ItemStack stack, Direction fromDirection);
	
}
