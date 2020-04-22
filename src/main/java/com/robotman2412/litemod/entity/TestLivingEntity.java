package com.robotman2412.litemod.entity;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.foods.FoodItem;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TestLivingEntity extends AnimalEntity {
	
	private static final Predicate<? super PlayerEntity> STONED_PREDICATE = (player)
			-> player.isAlive() && player.hasStatusEffect(FabricLitemod.HEALTH_CONFUSION);
	
	public TestLivingEntity(EntityType<TestLivingEntity> type, World world) {
		super(type, world);
		EntityDimensions dimensions = getDimensions(getPose());
		double width = dimensions.width;
		double height = dimensions.height;
		ignoreCameraFrustum = true;
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		getAttributes().get(EntityAttributes.MAX_HEALTH).setBaseValue(35 + world.random.nextInt(10));
		getAttributes().get(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
		getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1);
		getAttributes().register(EntityAttributes.ATTACK_SPEED).setBaseValue(1);
		getAttributes().get(EntityAttributes.ATTACK_KNOCKBACK).setBaseValue(1);
	}
	
	@Override
	protected void initGoals() {
		//this.goalSelector.add(0, new SwimGoal(this));
		//this.goalSelector.add(1, new AttackGoal(this));
		this.goalSelector.add(2, new RangedTemptGoal(this, 1.1D, Ingredient.ofItems(FoodItem.SNELPOEDER), false, 26D));
		//this.goalSelector.add(3, new WanderAroundGoal(this, 1, 40));
		//this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
	}
	
	@Override
	public LivingEntity getTarget() {
		List<PlayerEntity> ents = world.getEntities(EntityType.PLAYER,
				new Box(-10, -5, -10, 10, 5, 10).offset(getPos()
		), STONED_PREDICATE);
		return getClosest(ents);
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	public PlayerEntity getClosest(List<PlayerEntity> ents) {
		PlayerEntity closest = null;
		double maxSqrDist = Double.POSITIVE_INFINITY;
		for (PlayerEntity ent : ents) {
			double sqrDist = ent.squaredDistanceTo(this);
			if (sqrDist < maxSqrDist) {
				maxSqrDist = sqrDist;
				closest = ent;
			}
		}
		return closest;
	}
	
	@Override
	public boolean canMoveVoluntarily() {
		return true;
	}
	
	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}
	
	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
	}
	
	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
	}
	
	//region useless crap
	@Override
	public Iterable<ItemStack> getArmorItems() {
		return new ArrayList<>(); //no armor
	}
	
	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot) {
		return ItemStack.EMPTY; //no inventory
	}
	
	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		//nothing, no inventory
	}
	
	@Override
	public Arm getMainArm() {
		return Arm.RIGHT; //not really relevant
	}
	
	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		return null;
	}
	//endregion useless crap
	
}
