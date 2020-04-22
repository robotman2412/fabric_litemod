package com.robotman2412.litemod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@ModifyVariable(at = @At(value = "NEW", target = "Lnet/minecraft/client/util/math/Vector3f;"),
			method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", index = 9)
	public int nauseaInject(int localI) {
		if (client.player != null && client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
			//allows higher levels of nausea to make the world spin faster
			return 7 + client.player.getStatusEffect(StatusEffects.NAUSEA).getAmplifier();
		}
		return localI;
	}
	
}
