package com.robotman2412.litemod.weaopn;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public class Dragunov extends AbstractionOfTheGun {
	
	public Dragunov() {
		super(new Settings(), "dragunov_sniper_rifle");
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.usageTick(world, user, stack, remainingUseTicks);
		if (getEffectiveHoldTicks(stack, world) == 59 && !world.isClient) {
			SoundEvent sound = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
			world.playSound(null, user.getX(), user.getY(), user.getZ(), sound, SoundCategory.PLAYERS, 1, 0.125f);
		}
	}
	
	@Override
	public int getFireDelayTicks(WeaponMode mode) {
		return 40;
	}
	
	@Override
	public WeaponMode[] getPossibleModes(ItemStack stack) {
		return new WeaponMode[] {
				WeaponMode.SINGLE_FIRE
		};
	}
	
	@Override
	public int getMaximumDamage(ItemStack stack, World world) {
		return (int) (5 + getEffectiveHoldTicks(stack, world) / 60f * 15); //20 base damage when fully charged
	}
	
	public long getEffectiveHoldTicks(ItemStack stack, World world) {
		LastFireBullshite shite = map.get(stack);
		return Math.min(shite != null ? world.getTime() - shite.startedFiring : 0, 60); //at most 3 seconds
	}
	
	@Override
	public float getMinRecoil(ItemStack stack) {
		return 15;
	}
	
	@Override
	public float getMaxRecoil(ItemStack stack) {
		return 20;
	}
	
	@Override
	public int getBulletDiameter(ItemStack stack) {
		return 7;
	}
	
	@Override
	public int getMaxBulletLength(ItemStack stack) {
		return 54;
	}
	
	@Override
	protected double getMaximumDistance(ItemStack stack) {
		return 700;
	}
	
	@Override
	public boolean shouldFireOnRelease(ItemStack stack) {
		return true;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		int diam = getBulletDiameter(stack);
		int maxLen = getMaxBulletLength(stack);
		WeaponMode activeMode = getMode(stack);
		WeaponMode[] modes = getPossibleModes(stack);
		String sFireRate = "" + Math.round(20f / getFireDelayTicks(activeMode) * 100) / 100f;
		tooltip.add(new TranslatableText("robot_litemod.weapon.diameter").append("" + diam + "mm"));
		tooltip.add(new TranslatableText("robot_litemod.weapon.max_length").append("" + maxLen + "mm"));
		tooltip.add(new TranslatableText("robot_litemod.weapon.mode").append(new TranslatableText(activeMode.getTranslationKey())));
		tooltip.add(new TranslatableText("robot_litemod.weapon.rate_of_fire").append(sFireRate).append(new TranslatableText("robot_litemod.weapon.rate_of_fire.suffix")));
		tooltip.add(new TranslatableText("robot_litemod.weapon.remaining_ammo").append("" + getRemainingAmmo(stack)));
		tooltip.add(new TranslatableText("robot_litemod.weapon.damage.dragunov"));
	}
}
