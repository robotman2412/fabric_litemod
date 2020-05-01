package com.robotman2412.litemod.block.grave;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

public class TheStoneOfGravesBlockEntityRenderer extends BlockEntityRenderer<TheStoneOfGravesBlockEntity> {
	
	public TheStoneOfGravesBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(TheStoneOfGravesBlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrixStack.push();
		
		BlockState state = blockEntity.getWorld().getBlockState(blockEntity.getPos());
		if (!(state.getBlock() instanceof TheStoneOfGravesBlock)) {
			return;
		}
		Direction facingDirection = state.get(Properties.HORIZONTAL_FACING);
		matrixStack.translate(0.5, 0, 0.5);
		matrixStack.multiply(new Quaternion(new Vector3f(0, 1, 0), (float) (facingDirection.getHorizontal() * -Math.PI / 2), false));
		matrixStack.translate(0, 0.7, 5.999 / 16);
		
		if (blockEntity.lastKnownName != null) {
			drawCenteredText("      here lies      ", 0, -18, 0xffffff, false, matrixStack, vertexConsumers, false, 0, light);
			drawCenteredText(blockEntity.lastKnownName, 0, 0, 0xffffff, false, matrixStack, vertexConsumers, false, 0, light);
		}
		
		if (blockEntity.lastKnownOwnerName != null) {
			drawCenteredText("Beloved pet of", 0, 30, 0xffffff, false, matrixStack, vertexConsumers, false, 0, light);
			drawCenteredText(blockEntity.lastKnownOwnerName, 0, 36, 0xffffff, false, matrixStack, vertexConsumers, false, 0, light);
		}
		
		matrixStack.pop();
	}
	
	public void drawCenteredText(String text, float x, float y, int color, boolean shadow, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int backgroundColor, int light) {
		matrixStack.push();
		int theBigWidth = dispatcher.getTextRenderer().getStringWidth(text);
		float scaling = Math.min(0.5f / theBigWidth, 0.5f / 30);
		matrixStack.scale(-scaling, -scaling, -scaling);
		dispatcher.getTextRenderer().draw(text, -theBigWidth / 2f + x, y, color, shadow, matrixStack.peek().getModel(), vertexConsumerProvider, false, backgroundColor, light);
		matrixStack.pop();
	}
	
}
