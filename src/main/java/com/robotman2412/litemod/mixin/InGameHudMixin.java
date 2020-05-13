package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.util.Utils;
import com.robotman2412.litemod.weaopn.Dragunov;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
		if (getCameraPlayer() != null) {
			int level = 0;
			if (getCameraPlayer().hasStatusEffect(FabricLitemod.HEALTH_CONFUSION)) {
				StatusEffectInstance inst = getCameraPlayer().getStatusEffect(FabricLitemod.HEALTH_CONFUSION);
				level = (inst.getAmplifier() + 1) * 4;
			}
			long millis = System.currentTimeMillis();
			if (millis > flmLastUpdate + 50) {
				if (Utils.healthConfusion < level) {
					Utils.healthConfusion++;
				} else if (Utils.healthConfusion > level) {
					Utils.healthConfusion--;
				}
				flmLastUpdate = millis;
			}
		}
	}
	
	@Redirect(at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;blit(IIIIII)V"), method = "renderStatusBars()V")
	public void blitProxy(InGameHud hudOn, int x, int y, int u, int v, int width, int height) {
		if (getCameraPlayer() != null && Utils.healthConfusion > 0 && Utils.isHeartUv(u, v)) {
			float level = Utils.healthConfusion / 4f;
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
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"), method = "renderCrosshair")
	public float getAttackIndicatorProxy(ClientPlayerEntity player, float baseTime) {
		ItemStack stack = player.getActiveItem();
		if (player.isUsingItem() && stack.getItem() instanceof Dragunov) {
			Dragunov gunne = (Dragunov) stack.getItem();
			return gunne.getEffectiveHoldTicks(stack, player.world) / 60f;
		}
		return player.getAttackCooldownProgress(baseTime);
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"), method = "renderHotbar")
	public float getAttackIndicatorProxy0(ClientPlayerEntity player, float baseTime) {
		ItemStack stack = player.getActiveItem();
		if (player.isUsingItem() && stack.getItem() instanceof Dragunov) {
			Dragunov gunne = (Dragunov) stack.getItem();
			return gunne.getEffectiveHoldTicks(stack, player.world) / 60f;
		}
		return player.getAttackCooldownProgress(baseTime);
	}
	
}


