package com.robotman2412.litemod.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.PacketByteBuf;

public class CountedIngredient {
	
	public final Ingredient ingredient;
	public final int count;
	
	public CountedIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
		this.count = 1;
	}
	
	public CountedIngredient(Ingredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}
	
	public void write(PacketByteBuf buf) {
		ingredient.write(buf);
		buf.writeInt(count);
	}
	
	public boolean isApplicable(ItemStack stack) {
		return ingredient.test(stack);
	}
	
	public boolean containsEnough(Inventory inventory) {
		return getNumberApplicable(inventory) >= count;
	}
	
	public boolean containsEnough(Inventory inventory, int... usableSlots) {
		return getNumberApplicable(inventory, usableSlots) >= count;
	}
	
	public int getNumberApplicable(Inventory inventory) {
		int[] slotas = new int[inventory.getInvSize()];
		for (int i = 0; i < slotas.length; i++) {
			slotas[i] = i;
		}
		return getNumberApplicable(inventory, slotas);
	}
	
	public int getNumberApplicable(Inventory inventory, int... usableSlots) {
		int found = 0;
		for (int slot : usableSlots) {
			ItemStack stack = inventory.getInvStack(slot);
			if (ingredient.test(stack)) {
				found += stack.getCount();
			}
		}
		return found;
	}
	
	public static CountedIngredient fromJson(JsonElement jsonElement) {
		return fromJSON((JsonObject) jsonElement);
	}
	
	public static CountedIngredient fromJSON(JsonObject json) {
		int count = 1;
		if (json.has("count")) {
			count = json.get("count").getAsInt();
		}
		return new CountedIngredient(Ingredient.fromJson(json), count);
	}
	
	public static CountedIngredient fromTag(Tag<Item> tag, int count) {
		return new CountedIngredient(Ingredient.fromTag(tag), count);
	}
	
	public static CountedIngredient fromPacket(PacketByteBuf buf) {
		return new CountedIngredient(Ingredient.fromPacket(buf), buf.readInt());
	}
	
	public void consume(Inventory inventory) {
		int[] slotas = new int[inventory.getInvSize()];
		for (int i = 0; i < slotas.length; i++) {
			slotas[i] = i;
		}
		consume(inventory, slotas);
	}
	
	public void consume(Inventory inventory, int... usableSlots) {
		int countLeft = count;
		for (int slot : usableSlots) {
			ItemStack stack = inventory.getInvStack(slot);
			if (ingredient.test(stack)) {
				if (stack.getItem().hasRecipeRemainder()) {
					countLeft --;
					inventory.setInvStack(slot, new ItemStack(stack.getItem().getRecipeRemainder()));
					if (countLeft <= 0) {
						return;
					}
				}
				else
				{
					int taken = Math.min(stack.getCount(), countLeft);
					countLeft -= taken;
					stack.decrement(taken);
					if (countLeft <= 0) {
						return;
					}
				}
			}
		}
	}
	
}
