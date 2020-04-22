package com.robotman2412.litemod.foods;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.BlockWrapper;
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
	
	//region item
	public static final ItemWrapper BEIGETS = new FoodItem("beigets", 5, 5);
	public static final ItemWrapper BURGER_BUN = new FoodItem("burger_bun", 1, 2, 32);
	public static final ItemWrapper CHOCOLATE_AND_STRAWBERRY_ICE_CREAM = new FoodItem("chocolate_and_strawberry_ice_cream", 2, 8);
	public static final ItemWrapper CHOCOLATE_ICE_CREAM = new FoodItem("chocolate_ice_cream", 2, 8);
	public static final ItemWrapper CUPCAKE = new FoodItem("cupcake", 2, 2, 32);
	public static final ItemWrapper CUPCAKE_WITH_CHOCOLATE_FROSTING = new FoodItem("cupcake_with_chocolate_frosting", 2, 4);
	public static final ItemWrapper CUPCAKE_WITH_CHOCOLATE_FROSTING_AND_SPRINKLES = new FoodItem("cupcake_with_chocolate_frosting_and_sprinkles", 2, 4);
	public static final ItemWrapper CUPCAKE_WITH_PINK_FROSTING = new FoodItem("cupcake_with_pink_frosting", 2, 4);
	public static final ItemWrapper CUPCAKE_WITH_PINK_FROSTING_AND_SPRINKLES = new FoodItem("cupcake_with_pink_frosting_and_sprinkles", 2, 4);
	public static final ItemWrapper CUPCAKE_WITH_WHIPPED_CREAM_TOPPING = new FoodItem("cupcake_with_whipped_cream_topping", 2, 4);
	public static final ItemWrapper CUPCAKE_WITH_WHIPPED_CREAM_TOPPING_AND_SPRINKLES = new FoodItem("cupcake_with_whipped_cream_topping_and_sprinkles", 2, 4);
	public static final ItemWrapper DOUBLE_CHOCOLATE_ICE_CREAM = new FoodItem("double_chocolate_ice_cream", 2, 8);
	public static final ItemWrapper DOUBLE_STRAWBERRY_ICE_CREAM = new FoodItem("double_strawberry_ice_cream", 2, 8);
	public static final ItemWrapper HAMBURGER = new FoodItem("hamburger", 7, 8);
	public static final ItemWrapper ICE_CREAM_CONE = new FoodItem("ice_cream_cone", 1, 1, 32);
	public static final ItemWrapper LETTUCE = new FoodItem("lettuce", 2, 4, 32);
	public static final ItemWrapper PEPERONI_PIZZA = new FoodItem("peperoni_pizza", 6, 8);
	public static final ItemWrapper RAINBOW_COOKIE = new FoodItem("rainbow_cookie", 10, 10);
	public static final ItemWrapper STRAWBERRY_AND_CHOCOLATE_ICE_CREAM = new FoodItem("strawberry_and_chocolate_ice_cream", 2, 8);
	public static final ItemWrapper STRAWBERRY_ICE_CREAM = new FoodItem("strawberry_ice_cream", 2, 8);
	public static final ItemWrapper SNELPOEDER = new SNELPOEDER(); //TODO: apply potion effects
	public static final ItemWrapper TACCCO = new FoodItem("taccco", 6, 6);
	public static final ItemWrapper BAGEL = new FoodItem("bagel", 5, 3);
	public static final ItemWrapper CORN = new FoodItem("corn", 2, 2);
	//TODO: corn_seed as corn block block item
	public static final ItemWrapper POPCORN = new FoodItem("popcorn", 2, 6);
	public static final ItemWrapper SUGARSPINN = new FoodItem("sugarspinn", 2, 6);
	public static final ItemWrapper TOOHOT_DOGGO = new FoodItem("toohot_doggo", 5, 8);
	public static final ItemWrapper STRAWBERRY = new FoodItem("strawberry", 2, 4);
	public static final ItemWrapper TOMATO = new FoodItem("tomato", 2, 6);
	public static final ItemWrapper SLICED_TOMATO = new FoodItem("sliced_tomato", 1, 3);
	public static final ItemWrapper ICE_CREAM_SCOOP = new ItemWrapper(new Settings().group(FabricLitemod.KITCHEN_SUPPLIES), "ice_cream_scoop");
	public static final ItemWrapper PIZZA_SLICE = new FoodItem("pizza_slice", 1, 1.2f);
	public static final ItemWrapper CHOCOLATE_ICE = new FoodItem("chocolate_ice", 1, 6, 64);
	public static final ItemWrapper STRAWBERRY_ICE = new FoodItem("strawberry_ice", 1, 6, 64);
	
	/** all food-related items */
	public static final ItemWrapper[] ALL_ITEMS = new ItemWrapper[] {
			BEIGETS,
			BURGER_BUN,
			CHOCOLATE_AND_STRAWBERRY_ICE_CREAM,
			CHOCOLATE_ICE_CREAM,
			CUPCAKE,
			CUPCAKE_WITH_CHOCOLATE_FROSTING,
			CUPCAKE_WITH_CHOCOLATE_FROSTING_AND_SPRINKLES,
			CUPCAKE_WITH_PINK_FROSTING,
			CUPCAKE_WITH_PINK_FROSTING_AND_SPRINKLES,
			CUPCAKE_WITH_WHIPPED_CREAM_TOPPING,
			CUPCAKE_WITH_WHIPPED_CREAM_TOPPING_AND_SPRINKLES,
			DOUBLE_CHOCOLATE_ICE_CREAM,
			DOUBLE_STRAWBERRY_ICE_CREAM,
			HAMBURGER,
			ICE_CREAM_CONE,
			LETTUCE,
			PEPERONI_PIZZA,
			RAINBOW_COOKIE,
			STRAWBERRY_AND_CHOCOLATE_ICE_CREAM,
			STRAWBERRY_ICE_CREAM,
			SNELPOEDER,
			TACCCO,
			BAGEL,
			CORN,
			POPCORN,
			SUGARSPINN,
			TOOHOT_DOGGO,
			STRAWBERRY,
			TOMATO,
			SLICED_TOMATO,
			ICE_CREAM_SCOOP,
			PIZZA_SLICE,
			CHOCOLATE_ICE,
			STRAWBERRY_ICE
	};
	//endregion item
	
	//region block
	public static final BlockWrapper BLENDOMATOR_9000_WHITE = new BLENDOMATOR9000("white", "\u00A7f");
	public static final BlockWrapper BLENDOMATOR_9000_ORANGE = new BLENDOMATOR9000("orange", "\u00A76");
	public static final BlockWrapper BLENDOMATOR_9000_MAGENTA = new BLENDOMATOR9000("magenta", "\u00A75");
	public static final BlockWrapper BLENDOMATOR_9000_LIGHT_BLUE = new BLENDOMATOR9000("light_blue", "\u00A7b");
	public static final BlockWrapper BLENDOMATOR_9000_YELLOW = new BLENDOMATOR9000("yellow", "\u00A7e");
	public static final BlockWrapper BLENDOMATOR_9000_LIME = new BLENDOMATOR9000("lime", "\u00A7a");
	public static final BlockWrapper BLENDOMATOR_9000_PINK = new BLENDOMATOR9000("pink", "\u00A7d");
	public static final BlockWrapper BLENDOMATOR_9000_GRAY = new BLENDOMATOR9000("gray", "\u00A78");
	public static final BlockWrapper BLENDOMATOR_9000_LIGHT_GRAY = new BLENDOMATOR9000("light_gray", "\u00A77");
	public static final BlockWrapper BLENDOMATOR_9000_CYAN = new BLENDOMATOR9000("cyan", "\u00A73");
	public static final BlockWrapper BLENDOMATOR_9000_PURPLE = new BLENDOMATOR9000("purple", "\u00A75");
	public static final BlockWrapper BLENDOMATOR_9000_BLUE = new BLENDOMATOR9000("blue", "\u00A71");
	public static final BlockWrapper BLENDOMATOR_9000_BROWN = new BLENDOMATOR9000("brown", "\u00A76");
	public static final BlockWrapper BLENDOMATOR_9000_GREEN = new BLENDOMATOR9000("green", "\u00A7a");
	public static final BlockWrapper BLENDOMATOR_9000_RED = new BLENDOMATOR9000("red", "\u00A74");
	public static final BlockWrapper BLENDOMATOR_9000_BLACK = new BLENDOMATOR9000("black", "\u00A70");
	
	public static final BlockWrapper[] ALL_BLOCKS = {
			BLENDOMATOR_9000_WHITE,
			BLENDOMATOR_9000_ORANGE,
			BLENDOMATOR_9000_MAGENTA,
			BLENDOMATOR_9000_LIGHT_BLUE,
			BLENDOMATOR_9000_YELLOW,
			BLENDOMATOR_9000_LIME,
			BLENDOMATOR_9000_PINK,
			BLENDOMATOR_9000_GRAY,
			BLENDOMATOR_9000_LIGHT_GRAY,
			BLENDOMATOR_9000_CYAN,
			BLENDOMATOR_9000_PURPLE,
			BLENDOMATOR_9000_BLUE,
			BLENDOMATOR_9000_BROWN,
			BLENDOMATOR_9000_GREEN,
			BLENDOMATOR_9000_RED,
			BLENDOMATOR_9000_BLACK
	};
	//endregion block
	
}
