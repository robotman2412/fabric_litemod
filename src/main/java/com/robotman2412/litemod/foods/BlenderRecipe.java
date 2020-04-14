package com.robotman2412.litemod.foods;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.robotman2412.litemod.CountedIngredient;
import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BlenderRecipe implements Recipe<BLENDOMATOR9000BlockEntity> {
	
	public CountedIngredient[] ingredients;
	public int blendTicks;
	protected ItemStack out;
	public Identifier id;
	
	@Override
	public boolean matches(BLENDOMATOR9000BlockEntity inv, World world) {
		//ensure enough crap
		for (CountedIngredient ingredient : ingredients) {
			if (!ingredient.containsEnough(inv, 1, 2, 3)) {
				return false;
			}
		}
		//ensure no other crap
		ha: for (int i = 0; i < 3; i++) {
			ItemStack stack = inv.getInvStack(i + 1);
			if (!stack.isEmpty()) {
				for (CountedIngredient ingredient : ingredients) {
					if (ingredient.isApplicable(stack)) {
						continue ha;
					}
				}
				return false;
			}
		}
		return true;
	}
	
	@Override
	public ItemStack craft(BLENDOMATOR9000BlockEntity inv) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean fits(int width, int height) {
		return (height >= ingredients.length && width >= 1) || (width >= ingredients.length && height >= 1);
	}
	
	@Override
	public ItemStack getOutput() {
		return out.copy();
	}
	
	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	public RecipeSerializer<BlenderRecipe> getSerializer() {
		return Serializer.INSTANCE;
	}
	
	@Override
	public RecipeType<BlenderRecipe> getType() {
		return FabricLitemod.BLENDER_RECIPE_TYPE;
	}
	
	public void consume(BLENDOMATOR9000BlockEntity inventory) {
		for (CountedIngredient ingredient : ingredients) {
			ingredient.consume(inventory);
		}
	}
	
	public static class Serializer implements RecipeSerializer<BlenderRecipe> {
		
		public static final Serializer INSTANCE = new Serializer();
		
		protected Serializer() {
		
		}
		
		@Override
		public BlenderRecipe read(Identifier id, JsonObject json) {
			BlenderRecipe recipe = new BlenderRecipe();
			if (json.has("blend_ticks")) {
				recipe.blendTicks = json.get("blend_ticks").getAsInt();
			}
			else
			{
				recipe.blendTicks = 100;
			}
			JsonArray arr = json.getAsJsonArray("ingredients");
			recipe.ingredients = new CountedIngredient[arr.size()];
			for (int i = 0; i < recipe.ingredients.length; i++) {
				recipe.ingredients[i] = CountedIngredient.fromJson(arr.get(i));
			}
			JsonObject outObj = json.getAsJsonObject("output");
			String itemId = outObj.get("item").getAsString();
			int count = 1;
			if (outObj.has("count")) {
				count = outObj.get("count").getAsInt();
			}
			Item items = Registry.ITEM.get(new Identifier(itemId));
			if (items == Items.AIR) {
				throw new IllegalArgumentException("Wrong ID for output item: " + itemId);
			}
			recipe.out = new ItemStack(items, count);
			recipe.id = id;
			return recipe;
		}
		
		@Override
		public BlenderRecipe read(Identifier id, PacketByteBuf buf) {
			BlenderRecipe recipe = new BlenderRecipe();
			recipe.blendTicks = buf.readInt();
			int numIngredients = buf.readInt();
			recipe.ingredients = new CountedIngredient[numIngredients];
			for (int i = 0; i < numIngredients; i++) {
				recipe.ingredients[i] = CountedIngredient.fromPacket(buf);
			}
			recipe.out = buf.readItemStack();
			recipe.id = id;
			return recipe;
		}
		
		@Override
		public void write(PacketByteBuf buf, BlenderRecipe recipe) {
			buf.writeInt(recipe.blendTicks);
			buf.writeInt(recipe.ingredients.length);
			for (CountedIngredient ingredient : recipe.ingredients) {
				ingredient.write(buf);
			}
			buf.writeItemStack(recipe.out);
		}
	}
	
}
