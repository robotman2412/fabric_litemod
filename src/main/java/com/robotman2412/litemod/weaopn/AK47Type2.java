package com.robotman2412.litemod.weaopn;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AK47Type2 extends AbstractionOfTheGun {
	
	public AK47Type2() {
		super(new Settings(), "ak47_type2");
		addPropertyGetter(new Identifier("stage"), (stack, world, entity) -> {
			LastFireBullshite shite = map.get(stack);
			if (shite == null) {
				return 0;
			}
			if (world == null) {
				return 0;
			}
			long lastFired = shite.lastFireTick;
			long time = world.getTime();
			if (lastFired == time) {
				return 1;
			}
			else if (lastFired + 1 == time || lastFired + 2 == time) {
				return 2;
			}
			else if (getRemainingAmmo(stack) < 1) {
				return 2;
			}
			else
			{
				return 0;
			}
		});
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
	public int getMinimumDamage(ItemStack stack, World world) {
		return 4;
	}
	
	@Override
	public int getMaximumDamage(ItemStack stack, World world) {
		return 7;
	}
	
	@Override
	public float getMinRecoil(ItemStack stack) {
		return 1f;
	}
	
	@Override
	public float getMaxRecoil(ItemStack stack) {
		return 3.5f;
	}
	
	@Override
	public int getBulletDiameter(ItemStack stack) {
		return 7;
	}
	
	@Override
	public int getMaxBulletLength(ItemStack stack) {
		return 42;
	}
	
	@Override
	protected double getMaximumDistance(ItemStack stack) {
		return 175;
	}
	
}
