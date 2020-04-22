package com.robotman2412.litemod.util;

import net.minecraft.block.BlockState;

/** Applicable on block antities and blocks. */
public interface IRedstoneInfo {
	
	/** Differentiate between powered and powering components. */
	boolean isEmittedPower();
	/** Power level of 16 displays as on. */
	int getPowerLevel(BlockState state);
	
}
