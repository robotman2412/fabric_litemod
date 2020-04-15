package com.robotman2412.litemod.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	
	@Inject(at = @At(value = "INVOKE", target = ))
	public void nauseaInject() {
	
	}
	
}
