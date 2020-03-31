package com.robotman2412.litemod.foods;

import com.robotman2412.litemod.item.ItemWrapper;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemGroup;

public class FoodItem extends ItemWrapper {
	
	public FoodItem(String registryName, int hunger, float saturation) {
		super(new Settings().group(ItemGroup.FOOD).maxCount(1).food(new FoodComponent.Builder().hunger(hunger).saturationModifier(saturation).build()), registryName);
	}
	
	public FoodItem(String registryName, int hunger, float saturation, int maxCount) {
		super(new Settings().group(ItemGroup.FOOD).maxCount(maxCount).food(new FoodComponent.Builder().hunger(hunger).saturationModifier(saturation).build()), registryName);
	}
	
	public static FoodItem[] getAll() {
		return new FoodItem[] {
				new FoodItem("beigets", 5, 5),
				new FoodItem("burger_bun", 1, 2, 32),
				new FoodItem("chocolate_and_strawberry_ice_cream", 2, 8),
				new FoodItem("chocolate_ice_cream", 2, 8),
				new FoodItem("cupcake", 2, 2, 32),
				new FoodItem("cupcake_with_chocolate_frosting", 2, 4),
				new FoodItem("cupcake_with_chocolate_frosting_and_sprinkles", 2, 4),
				new FoodItem("cupcake_with_pink_frosting", 2, 4),
				new FoodItem("cupcake_with_pink_frosting_and_sprinkles", 2, 4),
				new FoodItem("cupcake_with_whipped_cream_topping", 2, 4),
				new FoodItem("cupcake_with_whipped_cream_topping_and_sprinkles", 2, 4),
				new FoodItem("double_chocolate_ice_cream", 2, 8),
				new FoodItem("double_strawberry_ice_cream", 2, 8),
				new FoodItem("hamburger", 7, 8),
				new FoodItem("ice_cream_cone", 1, 1, 32),
				new FoodItem("lettuce", 2, 4, 32),
				new FoodItem("peperoni_pizza", 6, 8),
				new FoodItem("rainbow_cookie", 10, 10),
				new FoodItem("strawberry_and_chocolate_ice_cream", 2, 8),
				new FoodItem("strawberry_ice_cream", 2, 8)
		};
	}
	
}
