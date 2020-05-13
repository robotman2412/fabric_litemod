package com.robotman2412.litemod.weaopn.ammo;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.item.ItemWrapper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

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
		return count;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		MunitionItem item = (MunitionItem) stack.getItem();
		int count = item.getCount(stack);
		int diam = item.type.diameter;
		int len = item.type.length;
		tooltip.add(new TranslatableText("robot_litemod.mag.bullets").append("" + count));
		tooltip.add(new TranslatableText("robot_litemod.mag.bullet_diameter").append("" + diam));
		tooltip.add(new TranslatableText("robot_litemod.mag.bullet_length").append("" + len));
	}
	
}
