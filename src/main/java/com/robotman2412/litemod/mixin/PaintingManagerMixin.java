package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.ClientEntry;
import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(PaintingManager.class)
public abstract class PaintingManagerMixin extends SpriteAtlasHolder {
	
	private PaintingManagerMixin(TextureManager textureManager, Identifier atlasId, String pathPrefix) {
		super(MinecraftClient.getInstance().getTextureManager(), new Identifier("ok_boomer"), "");
	}
	
	//@Inject(at = @At("HEAD"), method = "getPaintingSprite", cancellable = true, require = 0)
	public void onGetPaintingSprite(PaintingMotive motive, CallbackInfoReturnable<Sprite> cir) {
		if (!ClientEntry.communistEasterEgg) {
			return;
		}
		int x = motive.getWidth() / 16;
		int y = motive.getHeight() / 16;
		SpriteAtlasTexture atlas = getAltas(this);
		if (atlas == null) {
			return;
		}
		if (x == 1 && y == 1) {
			cir.setReturnValue(atlas.getSprite(new Identifier(FabricLitemod.MOD_ID, "textures/ussr_painting/1x1.png")));
		}
		else if (x == 1 && y == 2) {
			cir.setReturnValue(atlas.getSprite(new Identifier(FabricLitemod.MOD_ID, "textures/ussr_painting/1x2.png")));
		}
		else if (x == 2 && y == 2) {
			cir.setReturnValue(atlas.getSprite(new Identifier(FabricLitemod.MOD_ID, "textures/ussr_painting/2x2.png")));
		}
		else if (x == 4 && y == 2) {
			cir.setReturnValue(atlas.getSprite(new Identifier(FabricLitemod.MOD_ID, "textures/ussr_painting/4x2.png")));
		}
		else if (x == 4 && y == 4) {
			cir.setReturnValue(atlas.getSprite(new Identifier(FabricLitemod.MOD_ID, "textures/ussr_painting/4x4.png")));
		}
	}
	
	private static SpriteAtlasTexture getAltas(Object objectFor) {
		Class<SpriteAtlasHolder> holder = SpriteAtlasHolder.class;
		try {
			Field field = holder.getDeclaredField("atlas");
			field.setAccessible(true);
			return (SpriteAtlasTexture) field.get(objectFor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
