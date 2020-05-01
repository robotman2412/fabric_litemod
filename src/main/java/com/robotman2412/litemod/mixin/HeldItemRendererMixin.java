package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.mixinutil.CanCancelStuff;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	
	@Inject(at = @At("HEAD"), method = "resetEquipProgress", cancellable = true)
	public void onResetEquipProgress(Hand hand, CallbackInfo ci) {
		ItemStack stack = MinecraftClient.getInstance().player.getStackInHand(hand);
		if (stack != null && !stack.isEmpty() && stack.getItem() instanceof CanCancelStuff && ((CanCancelStuff) stack.getItem()).cancelEquipProgressReset(stack)) {
			ci.cancel();
		}
	}
	
}
