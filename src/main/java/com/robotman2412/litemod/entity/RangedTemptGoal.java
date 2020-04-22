package com.robotman2412.litemod.entity;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.EnumSet;

public class RangedTemptGoal extends Goal {
	public TargetPredicate temptingEntityPredicate;
	protected final MobEntityWithAi mob;
	private final double speed;
	private double lastPlayerX;
	private double lastPlayerY;
	private double lastPlayerZ;
	private double lastPlayerPitch;
	private double lastPlayerYaw;
	protected PlayerEntity closestPlayer;
	private int cooldown;
	private boolean active;
	private final Ingredient food;
	private final boolean canBeScared;
	
	public RangedTemptGoal(MobEntityWithAi mob, double speed, Ingredient food, boolean canBeScared, double range) {
		this(mob, speed, canBeScared, food, range);
	}
	
	public RangedTemptGoal(MobEntityWithAi mob, double speed, boolean canBeScared, Ingredient food, double range) {
		this.mob = mob;
		this.speed = speed;
		this.food = food;
		this.canBeScared = canBeScared;
		this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
		if (!(mob.getNavigation() instanceof MobNavigation) && !(mob.getNavigation() instanceof BirdNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
		}
		temptingEntityPredicate = (new TargetPredicate()).setBaseMaxDistance(range).includeInvulnerable().includeTeammates().ignoreEntityTargetRules().includeHidden();
	}
	
	public boolean canStart() {
		if (this.cooldown > 0) {
			--this.cooldown;
			return false;
		} else {
			this.closestPlayer = this.mob.world.getClosestPlayer(temptingEntityPredicate, this.mob);
			if (this.closestPlayer == null) {
				return false;
			} else {
				return this.isTempedBy(this.closestPlayer.getMainHandStack()) || this.isTempedBy(this.closestPlayer.getOffHandStack());
			}
		}
	}
	
	protected boolean isTempedBy(ItemStack stack) {
		return this.food.test(stack);
	}
	
	public boolean shouldContinue() {
		if (this.canBeScared()) {
			if (this.mob.squaredDistanceTo(this.closestPlayer) < 36.0D) {
				if (this.closestPlayer.squaredDistanceTo(this.lastPlayerX, this.lastPlayerY, this.lastPlayerZ) > 0.010000000000000002D) {
					return false;
				}
				
				if (Math.abs((double)this.closestPlayer.pitch - this.lastPlayerPitch) > 5.0D || Math.abs((double)this.closestPlayer.yaw - this.lastPlayerYaw) > 5.0D) {
					return false;
				}
			} else {
				this.lastPlayerX = this.closestPlayer.getX();
				this.lastPlayerY = this.closestPlayer.getY();
				this.lastPlayerZ = this.closestPlayer.getZ();
			}
			
			this.lastPlayerPitch = (double)this.closestPlayer.pitch;
			this.lastPlayerYaw = (double)this.closestPlayer.yaw;
		}
		
		return this.canStart();
	}
	
	protected boolean canBeScared() {
		return this.canBeScared;
	}
	
	public void start() {
		this.lastPlayerX = this.closestPlayer.getX();
		this.lastPlayerY = this.closestPlayer.getY();
		this.lastPlayerZ = this.closestPlayer.getZ();
		this.active = true;
	}
	
	public void stop() {
		this.closestPlayer = null;
		this.mob.getNavigation().stop();
		this.cooldown = 100;
		this.active = false;
	}
	
	public void tick() {
		this.mob.getLookControl().lookAt(this.closestPlayer, (float)(this.mob.getBodyYawSpeed() + 20), (float)this.mob.getLookPitchSpeed());
		if (this.mob.squaredDistanceTo(this.closestPlayer) < 6.25D) {
			this.mob.getNavigation().stop();
		} else {
			this.mob.getNavigation().startMovingTo(this.closestPlayer, this.speed);
		}
		
	}
	
	public boolean isActive() {
		return this.active;
	}
}
