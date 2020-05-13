package com.robotman2412.litemod.weaopn;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Makarov extends AbstractionOfTheGun {
	
	public Makarov() {
		super(new Settings(), "makarov_pistol");
		addPropertyGetter(new Identifier("stage"), (stack, world, entity) -> {
			if (getRemainingAmmo(stack) < 1) {
				return 1;
			}
			else
			{
				LastFireBullshite shite = map.get(stack);
				if (shite != null && world != null && shite.lastFireTick + 3 > world.getTime()) {
					return 1;
				}
			}
			return 0;
		});
	}
	
	@Override
	public int getFireDelayTicks(WeaponMode mode) {
		return 13;
	}
	
	@Override
	public WeaponMode[] getPossibleModes(ItemStack stack) {
		return new WeaponMode[] {
				WeaponMode.SINGLE_FIRE
		};
	}
	
	@Override
	public int getMinimumDamage(ItemStack stack, World world) {
		return 5;
	}
	
	@Override
	public int getMaximumDamage(ItemStack stack, World world) {
		return 7;
	}
	
	@Override
	public float getMinRecoil(ItemStack stack) {
		return 0.5f;
	}
	
	@Override
	public float getMaxRecoil(ItemStack stack) {
		return 2f;
	}
	
	@Override
	public int getBulletDiameter(ItemStack stack) {
		return 9;
	}
	
	@Override
	public int getMaxBulletLength(ItemStack stack) {
		return 20;
	}
	
	@Override
	protected double getMaximumDistance(ItemStack stack) {
		return 35;
	}
	
}
