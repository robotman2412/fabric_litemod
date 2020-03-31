package com.robotman2412.litemod.item.superweapon;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;

public class SuperWeaponSwordItem extends SwordItem {
	
	public SuperWeaponSwordItem() {
		super(SuperWeapons.MATERIAL, 5, -2, new Settings().maxDamage(SuperWeapons.MATERIAL.getDurability()).group(ItemGroup.COMBAT));
	}
	
}
