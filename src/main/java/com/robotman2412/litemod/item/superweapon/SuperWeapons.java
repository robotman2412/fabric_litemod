package com.robotman2412.litemod.item.superweapon;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SuperWeapons implements ToolMaterial {
	
	public static final SuperWeapons MATERIAL = new SuperWeapons();
	//region material
	protected SuperWeapons() {
	
	}
	
	@Override
	public int getDurability() {
		return 1761;
	}
	
	@Override
	public float getMiningSpeed() {
		return 8;
	}
	
	@Override
	public float getAttackDamage() {
		return 5f;
	}
	
	@Override
	public int getMiningLevel() {
		return 3;
	}
	
	@Override
	public int getEnchantability() {
		return 28;
	}
	
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.EMPTY;
	}
	//endregion material
	
	public static final SuperWeaponSwordItem SWORD = new SuperWeaponSwordItem();
	public static final SuperWeaponAxeItem AXE = new SuperWeaponAxeItem();
	
	public static void registerAll() {
		Registry.register(Registry.ITEM, new Identifier(FabricLitemod.MOD_ID, "superweapon_sword"), SWORD);
		Registry.register(Registry.ITEM, new Identifier(FabricLitemod.MOD_ID, "superweapon_axe"), AXE);
	}
	
}
