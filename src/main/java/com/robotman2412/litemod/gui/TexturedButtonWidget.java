package com.robotman2412.litemod.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.Identifier;

public class TexturedButtonWidget extends AbstractButtonWidget {
	
	public Identifier texture;
	public Runnable onPress;
	public TexturePosition uv;
	
	public TexturedButtonWidget(int x, int y, int width, int height, TexturePosition uv, Identifier texture, Runnable onPress) {
		super(x, y, width, height, "");
		this.texture = texture;
		this.onPress = onPress;
		this.uv = uv;
	}
	
	public TexturedButtonWidget(int x, int y, int width, int height, TexturePosition uv, Identifier texture) {
		super(x, y, width, height, "");
		this.texture = texture;
		this.uv = uv;
	}
	
	protected TexturedButtonWidget(int x, int y, int width, int height, Runnable onPress) {
		super(x, y, width, height, "");
		this.onPress = onPress;
	}
	
	protected TexturedButtonWidget(int x, int y, int width, int height) {
		super(x, y, width, height, "");
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(getTexture());
		TexturePosition uv = getTexturePosition();
		if (clicked(mouseX, mouseY)) {
			blit(x, y, uv.u2, uv.v2, width, height);
		}
		else
		{
			blit(x, y, uv.u, uv.v, width, height);
		}
	}
	
	public TexturePosition getTexturePosition() {
		return uv;
	}
	
	public Identifier getTexture() {
		return texture;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean clicked = super.mouseClicked(mouseX, mouseY, button);
		if (clicked && onPress != null) {
			onPress.run();
		}
		return clicked;
	}
	
	public static class TexturePosition {
		
		final int u;
		final int v;
		final int u2;
		final int v2;
		
		public TexturePosition(int u, int v, int u2, int v2) {
			this.u = u;
			this.v = v;
			this.u2 = u2;
			this.v2 = v2;
		}
		
	}
	
}
