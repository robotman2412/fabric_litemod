package com.robotman2412.litemod.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "net/minecraft/container/PlayerContainer$1")
public class PlayerContainerEquipmentSlotMixin {
	
	/**
	 * @author RobotMan2412
	 */
	@Overwrite()
	public boolean canInsert(ItemStack stack) {
		return true;
	}
	
}
