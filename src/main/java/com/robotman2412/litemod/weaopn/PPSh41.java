package com.robotman2412.litemod.weaopn;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PPSh41 extends AbstractionOfTheGun {
	
	public PPSh41() {
		super(new Settings(), "ppsh41_smg");
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
		return 1;
	}
	
	@Override
	public WeaponMode[] getPossibleModes(ItemStack stack) {
		return new WeaponMode[] {
				WeaponMode.AUTOMATIC,
				WeaponMode.TRI_SHOT
		};
	}
	
	@Override
	public int getMinimumDamage(ItemStack stack, World world) {
		return 2;
	}
	
	@Override
	public int getMaximumDamage(ItemStack stack, World world) {
		return 4;
	}
	
	@Override
	public float getMinRecoil(ItemStack stack) {
		return 1;
	}
	
	@Override
	public float getMaxRecoil(ItemStack stack) {
		return 3;
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
		return 125;
	}
	
}
