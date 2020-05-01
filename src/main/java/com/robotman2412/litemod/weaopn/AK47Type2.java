package com.robotman2412.litemod.weaopn;

import net.minecraft.item.ItemStack;

public class AK47Type2 extends AbstractionOfTheGun {
	
	public AK47Type2() {
		super("ak47_type2");
	}
	
	@Override
	public int getFireDelayTicks(WeaponMode mode) {
		if (mode == WeaponMode.AUTOMATIC) {
			return 3;
		}
		else if (mode == WeaponMode.SINGLE_FIRE) {
			return 10;
		}
		else
		{
			return 20;
		}
	}
	
	@Override
	public WeaponMode[] getPossibleModes(ItemStack stack) {
		return new WeaponMode[] {
				WeaponMode.AUTOMATIC,
				WeaponMode.SINGLE_FIRE
		};
	}
	
	@Override
	public int getMinimumDamage(ItemStack stack) {
		return 3;
	}
	
	@Override
	public int getMaximumDamage(ItemStack stack) {
		return 5;
	}
	
	@Override
	protected double getMaximumDistance(ItemStack stack) {
		return 175;
	}
	
}
