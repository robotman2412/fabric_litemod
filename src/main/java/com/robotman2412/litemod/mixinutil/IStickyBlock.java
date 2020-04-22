package com.robotman2412.litemod.mixinutil;

import net.minecraft.block.Block;

public interface IStickyBlock {
	
	default boolean willStickToOther(Block other) {
		return true;
	}
	
}
