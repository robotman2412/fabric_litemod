package com.robotman2412.litemod.gui;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BLENDOMATOR9000Screen extends ContainerScreen<BLENDOMATOR9000Container> {
	
	public static final Identifier BACKGROUND = new Identifier(FabricLitemod.MOD_ID, "textures/gui/blendomator_9000.png");
	
	public float rotatedDelta;
	public int rotated;
	
	public BLENDOMATOR9000Screen(BLENDOMATOR9000Container container) {
		super(container, MinecraftClient.getInstance().player.inventory, new LiteralText("The BLENDOMATOR 9000!"));
		containerWidth = 176;
		containerHeight = 166;
	}
	
	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		rotatedDelta += delta;
		rotated = (int) rotatedDelta;
		minecraft.getTextureManager().bindTexture(BACKGROUND);
		//background
		blit(x, y, 0, 0, containerWidth, containerHeight);
		
		//fuel slot
		int maxFuelHeight = 14;
		int fuelHeight = container.tileEnt.fuelLeft * maxFuelHeight / container.tileEnt.maxFuelLeft;
		blit(x + 72, y + 50 - fuelHeight, 178, 35 - fuelHeight, 14, fuelHeight);
		
		//progress slot
		int maxProgressWidth = 22;
		int progressWidth = container.tileEnt.progress * maxProgressWidth / container.tileEnt.maxProgress;
		blit(x + 68, y + 18, 179, 2, progressWidth, 16);
		
		if (container.tileEnt.nowBlending != null) {
			float totalCount = 0;
			for (int i = 0; i < 3; i++) {
				ItemStack stack = container.tileEnt.getInvStack(i + 1);
				totalCount += stack.getCount() / (float) stack.getMaxCount();
			}
			float heightPerThingy = 46f / totalCount;
			int bottomOffset = 0;
			int stage = (int) (container.tileEnt.progress * 3.6f / container.tileEnt.maxProgress);
			for (int i = 0; i < 3; i++) {
				ItemStack stack = container.tileEnt.getInvStack(i + 1);
				if (!container.tileEnt.isRecipeItem(stack)) {
					continue;
				}
				int leHeight = Math.round(heightPerThingy * stack.getCount() / (float) stack.getMaxCount());
				drawIngredient(Registry.ITEM.getId(stack.getItem()), bottomOffset, leHeight, stage);
				bottomOffset += leHeight;
			}
		}
		
		String sTitle = getTitle().asFormattedString();
		float txWidth = font.getStringWidth(sTitle);
		font.draw(sTitle, x + containerWidth / 2f - txWidth / 2f, y + 6, 0x404040);
		
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		drawMouseoverTooltip(mouseX, mouseY);
	}
	
	public void drawIngredient(Identifier id, int bottomOffset, int height, int stage) {
		Identifier location = new Identifier(id.getNamespace(), "textures/blender/" + id.getPath() + ".png");
		minecraft.getTextureManager().bindTexture(location);
		for (int i = height; i > 0; i -= 16) {
			drawIngredient0(bottomOffset, Math.min(i, 16), stage);
			bottomOffset += 16;
		}
	}
	
	private void drawIngredient0(int bottomOffset, int height, int stage) {
		int y = this.y + 70 - bottomOffset - height;
		int rot = rotated & 15;
		int x0 = this.x + 94 + rot;
		int x1 = this.x + 94;
		int u0 = 0;
		int u1 = 16 - rot;
		int v = stage * 16;
		int width0 = 16 - rot;
		int width1 = rot;
		blit(x0, y, u0, v, width0, height, 16, 64);
		blit(x1, y, u1, v, width1, height, 16, 64);
	}
	
}
