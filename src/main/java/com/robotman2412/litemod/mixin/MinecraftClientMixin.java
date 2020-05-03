package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.ClientEntry;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	
	@Inject(at = @At("HEAD"), method = "getWindowTitle", cancellable = true, require = 0)
	public void onGetWindowTitle(CallbackInfoReturnable<String> cir) {
		if (ClientEntry.shouldOverrideWindowTitle) {
			cir.setReturnValue(ClientEntry.getWindowTitle());
		}
	}
	
}
