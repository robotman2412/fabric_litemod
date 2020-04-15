package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.gui.DrunknessHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper {
	
	@Shadow private PlayerEntity getCameraPlayer() {return null;}
	
	@Shadow private int scaledWidth;
	
	private long flmLastUpdate;
	
	@Inject(at = @At("HEAD"), method = "renderStatusBars()V")
	public void onRenderStatusBars(CallbackInfo ci) {
		GameRe
		if (getCameraPlayer() != null) {
			int level = 0;
			if (getCameraPlayer().hasStatusEffect(FabricLitemod.HEALTH_CONFUSION)) {
				StatusEffectInstance inst = getCameraPlayer().getStatusEffect(FabricLitemod.HEALTH_CONFUSION);
				level = (inst.getAmplifier() + 1) * 4;
			}
			long millis = System.currentTimeMillis();
			if (millis > flmLastUpdate + 50) {
				if (DrunknessHelper.healthConfusion < level) {
					DrunknessHelper.healthConfusion++;
				} else if (DrunknessHelper.healthConfusion > level) {
					DrunknessHelper.healthConfusion--;
				}
				flmLastUpdate = millis;
			}
		}
	}
	
	@Redirect(at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;blit(IIIIII)V"), method = "renderStatusBars()V")
	public void blitProxy(InGameHud hudOn, int x, int y, int u, int v, int width, int height) {
		if (getCameraPlayer() != null && DrunknessHelper.healthConfusion > 0 && DrunknessHelper.isHeartUv(u, v)) {
			float level = DrunknessHelper.healthConfusion / 4f;
			long millis = System.currentTimeMillis();
			double time0 = 1500f + (x - scaledWidth / 2.5 + y % 10);
			double angle0 = millis % time0 / time0 * Math.PI * 2;
			double time1 = new Random(x * 26758 * (y >> 2) * 23674).nextDouble() * 2700 + 500;
			double angle1 = millis % time1 / time1 * Math.PI * 2;
			double extent = Math.sin(angle1) * level;
			if (extent < 0) {
				extent = -extent;
				angle0 = -angle0;
			}
			x += Math.round(Math.sin(angle0) * extent);
			y += Math.round(Math.cos(angle0) * extent);
		}
		hudOn.blit(x, y, u, v, width, height);
	}
	
}


