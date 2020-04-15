package com.robotman2412.litemod.foods;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.item.ItemWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

public class SNELPOEDER extends ItemWrapper {
	
	public SNELPOEDER() {
		super(new Settings().group(FabricLitemod.KITCHEN_SUPPLIES).food(
				new FoodComponent.Builder().alwaysEdible().build()
		), "snelpoeder");
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user.world.isClient) {
			user.sendMessage(new LiteralText("HA! cocaine."));
		}
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 1, true, false));
		user.addStatusEffect(new StatusEffectInstance(FabricLitemod.HEALTH_CONFUSION, 400, 20, true, false));
		stack.decrement(1);
		return stack;
	}
	
}
