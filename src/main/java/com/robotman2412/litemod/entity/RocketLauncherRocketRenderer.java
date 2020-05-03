package com.robotman2412.litemod.entity;

import com.robotman2412.litemod.FabricLitemod;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RocketLauncherRocketRenderer extends EntityRenderer<RocketLauncherRocket> {
	
	public RocketLauncherRocketRenderer(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
		super(dispatcher);
	}
	
	@Override
	public void render(RocketLauncherRocket entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
		World world = entity.world;
		
		BlockState state = Blocks.STONE.getDefaultState();
		
		matrixStack.push();
		matrixStack.translate(-0.5D, 0.0D, -0.5D);
		BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		blockRenderManager.renderBlockAsEntity(state, matrixStack, vertexConsumers, light, 0);
		matrixStack.pop();
	}
	
	@Override
	public Identifier getTexture(RocketLauncherRocket entity) {
		return new Identifier(FabricLitemod.MOD_ID, "rocket_launcher_rocket");
	}
	
}
