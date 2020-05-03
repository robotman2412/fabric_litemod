package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.ClientEntry;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	
	@Shadow
	private String splashText;
	
	@Inject(at = @At("RETURN"), method = "init", require = 0)
	public void onInit(CallbackInfo ci) {
		if (ClientEntry.communistEasterEgg) {
			int textVariant = new Random().nextInt(ClientEntry.communistEasterEggSplashes.size());
			try {
				splashText = ClientEntry.communistEasterEggSplashes.get(textVariant);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
