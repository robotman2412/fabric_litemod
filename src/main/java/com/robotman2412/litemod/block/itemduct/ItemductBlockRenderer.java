package com.robotman2412.litemod.block.itemduct;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;

public class ItemductBlockRenderer<Duct extends AbstractItemductBlockEntity> extends BlockEntityRenderer<Duct> {
	
	public ItemRenderer itemRenderer;
	
	public ItemductBlockRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
		itemRenderer = MinecraftClient.getInstance().getItemRenderer();
	}
	
	@Override
	public void render(Duct blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrixStack.push();
		matrixStack.translate(0.5, 0.5, 0.5);
		for (ItemDuctItem item : blockEntity.items) {
			matrixStack.push();
			double x = 0;
			double y = 0;
			double z = 0;
			if (item.direction != null) {
				double dTravelingToCenter = item.isTravelingToCenter ? -blockEntity.getItemSpeed() : blockEntity.getItemSpeed();
				double offsX = item.direction.getOffsetX();
				double offsY = item.direction.getOffsetY();
				double offsZ = item.direction.getOffsetZ();
				x = offsX * item.distanceFromCenter + offsX * tickDelta * dTravelingToCenter;
				y = offsY * item.distanceFromCenter + offsY * tickDelta * dTravelingToCenter;
				z = offsZ * item.distanceFromCenter + offsZ * tickDelta * dTravelingToCenter;
				x /= 16;
				y /= 16;
				z /= 16;
			}
			matrixStack.translate(x, y, z);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			double angle = (System.currentTimeMillis() % 3000) / 3000d * Math.PI;
			Quaternion rotatables = new Quaternion(Vector3f.POSITIVE_Y, (float) angle, false);
			matrixStack.multiply(rotatables);
			itemRenderer.renderItem(item.stack, ModelTransformation.Mode.FIXED, light, overlay, matrixStack, vertexConsumers);
			matrixStack.pop();
		}
		matrixStack.pop();
	}
	
}
