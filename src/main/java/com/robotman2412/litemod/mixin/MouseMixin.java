package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.weaopn.Dragunov;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
	public void redirectLookUpdate(ClientPlayerEntity entity, double cursorDeltaX, double cursorDeltaY) {
		ClientPlayerEntity player = client.player;
		if (player != null && player.isUsingItem()) {
			ItemStack using = player.getActiveItem();
			if (using.getItem() instanceof Dragunov) {
				cursorDeltaX *= 0.5;
				cursorDeltaY *= 0.5;
			}
		}
		entity.changeLookDirection(cursorDeltaX, cursorDeltaY);
	}
	
}
