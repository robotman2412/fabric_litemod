package com.robotman2412.litemod.block.itemduct;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;

public class ItemductBlockRenderer<Duct extends AbstractItemductBlockEntity> extends BlockEntityRenderer<Duct> {
	
	public ItemRenderer itemRenderer;
	
	public ItemductBlockRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
		itemRenderer = MinecraftClient.getInstance().getItemRenderer();
	}
	
	@Override
	public void render(Duct blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		
		for (ItemDuctItem item : blockEntity.items) {
			matrixStack.translate(0.5, 0.5, 0.5);
			double x = item.direction.getOffsetX() * item.distanceFromCenter / 16d;
			double y = -item.direction.getOffsetY() * item.distanceFromCenter / 16d;
			double z = item.direction.getOffsetZ() * item.distanceFromCenter / 16d;
			matrixStack.translate(x, y, z);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			
			itemRenderer.renderItem(item.stack, ModelTransformation.Mode.FIXED, light, overlay, matrixStack, vertexConsumers);
		}
	}
	
}
