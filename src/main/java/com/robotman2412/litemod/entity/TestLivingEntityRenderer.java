package com.robotman2412.litemod.entity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class TestLivingEntityRenderer extends MobEntityRenderer<TestLivingEntity, TestLivingEntityModel> {
	
	public static Identifier TEXTURE = new Identifier("robot_litemod", "textures/entity/dragon/dragon_0.png");
	
	public TestLivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityRendererRegistry.Context context) {
		super(entityRenderDispatcher, new TestLivingEntityModel(), 2f);
	}
	
	@Override
	public Identifier getTexture(TestLivingEntity entity) {
		return TEXTURE;
	}
	
}
