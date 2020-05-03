package com.robotman2412.litemod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class RocketLauncherRocket extends ProjectileEntity {
	
	public Entity theBigLauncher;
	
	public RocketLauncherRocket(EntityType<? extends ProjectileEntity> type, World world) {
		super(type, world);
	}
	
	@Override
	public boolean hasNoGravity() {
		return true;
	}
	
	@Override
	protected void onHit(HitResult hitResult) {
		if (hitResult.getType() != HitResult.Type.MISS) {
			explodonate();
		}
	}
	
	public void explodonate() {
		world.createExplosion(theBigLauncher, getX(), getY(), getZ(), 1, Explosion.DestructionType.BREAK);
		remove();
	}
	
	@Override
	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}
	
}
