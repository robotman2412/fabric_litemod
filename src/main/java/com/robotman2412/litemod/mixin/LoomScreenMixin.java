package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.util.Utils;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LoomScreen.class)
public class LoomScreenMixin {
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;size()I"), method = "onInventoryChanged")
	public int getPatternsSizeProxy(ListTag list) {
		return list.size() + 6 - Utils.maxBannerThingies;
	}
	
}
