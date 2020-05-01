package com.robotman2412.litemod.mixinutil;

import net.minecraft.item.ItemStack;

public interface CanCancelStuff {
	
	default boolean cancelEquipProgressReset(ItemStack stack) {
		return false;
	}
	
}
