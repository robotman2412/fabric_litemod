package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.ClientEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(at = @At("HEAD"), method = "render")
	public void onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && tickDelta > 0.0001) {
			float mult = (float) Math.pow(ClientEntry.getRecoilStrength(), tickDelta);
			float pitched = ClientEntry.remainingRecoilPitch - ClientEntry.remainingRecoilPitch * mult;
			float yawed = ClientEntry.remainingRecoilYaw - ClientEntry.remainingRecoilYaw * mult;
			ClientEntry.remainingRecoilPitch -= pitched;
			ClientEntry.remainingRecoilYaw -= yawed;
			double recoilBacktreshold = ClientEntry.recoilBackThreshold();
			if (Math.abs(pitched) < recoilBacktreshold) {
				pitched = -pitched;
			}
			if (Math.abs(yawed) < recoilBacktreshold) {
				yawed = -yawed;
			}
			player.pitch += pitched;
			player.yaw += yawed;
		}
	}
	
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
