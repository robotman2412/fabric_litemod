package com.robotman2412.litemod.item.superweapon;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemGroup;

public class SuperWeaponAxeItem extends AxeItem {
	
	public SuperWeaponAxeItem() {
		super(SuperWeapons.MATERIAL, 8, -2.6f, new Settings().group(ItemGroup.COMBAT).maxDamage(SuperWeapons.MATERIAL.getDurability()));
	}
	
}
