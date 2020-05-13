package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.util.Utils;
import net.minecraft.container.LoomContainer;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LoomContainer.class)
public class LoomContainerMixin {
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;size()I"), method = "Lnet/minecraft/container/LoomContainer;onContentChanged(Lnet/minecraft/inventory/Inventory;)V")
	public int getPatternsSizeProxy(ListTag list) {
		return list.size() + 6 - Utils.maxBannerThingies;
	}
	
}
