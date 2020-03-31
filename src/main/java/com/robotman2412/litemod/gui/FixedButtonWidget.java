package com.robotman2412.litemod.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.math.MathHelper;

public class FixedButtonWidget extends ButtonWidget {
	
	public FixedButtonWidget(int x, int y, int width, int height, String message, PressAction onPress) {
		super(x, y, width, height, message, onPress);
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		
		//FIX IS HERE:
		//TODO: make a mixin or something for this?
		int heightThingy = Math.max(3, height - 15);
		int leftWidth = width / 2;
		int rightWidth = width - leftWidth;
		
		this.blit(this.x, this.y, 0, 46 + i * 20, leftWidth, this.height - heightThingy);
		this.blit(this.x + leftWidth, this.y, 200 - rightWidth, 46 + i * 20, rightWidth, this.height - heightThingy);
		
		this.blit(this.x, this.y + height - heightThingy, 0, 46 + i * 20 + 20 - heightThingy, leftWidth, heightThingy);
		this.blit(this.x + this.width / 2, this.y + height - heightThingy, 200 - rightWidth, 46 + i * 20 + 20 - heightThingy, rightWidth, heightThingy);
		//END FIX
		
		this.renderBg(minecraftClient, mouseX, mouseY);
		int j = this.active ? 16777215 : 10526880;
		this.drawCenteredString(textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}
	
}
