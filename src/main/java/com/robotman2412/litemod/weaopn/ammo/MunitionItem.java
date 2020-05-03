package com.robotman2412.litemod.weaopn.ammo;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.item.ItemWrapper;
import net.minecraft.item.ItemStack;

public class MunitionItem extends ItemWrapper {
	
	public MunitionType type;
	public int count;
	
	public MunitionItem(MunitionType type, int count, String itemName) {
		super(new Settings(), itemName);
		this.type = type;
		this.count = count;
	}
	
	public MunitionItem(ItemWrapper remainder, MunitionType type, int count, String itemName) {
		super(new Settings().recipeRemainder(remainder).group(FabricLitemod.WEAPONS), itemName);
		this.type = type;
		this.count = count;
	}
	
	public int getCount(ItemStack stack) {
		return count - stack.getDamage();
	}
	
}
