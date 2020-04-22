package com.robotman2412.litemod.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.function.Consumer;

public class CustomItemGroupAppender implements Consumer<List<ItemStack>> {
	
	public String ID;
	
	protected CustomItemGroupAppender() {
	
	}
	
	public CustomItemGroupAppender(Identifier ID) {
		this.ID = ID.getNamespace() + "." + ID.getPath();
	}
	
	public CustomItemGroupAppender(String nameSpace, String path) {
		this.ID = nameSpace + "." + path;
	}
	
	public CustomItemGroupAppender(String ID) {
		this.ID = ID;
	}
	
	@Override
	public void accept(List<ItemStack> itemStacks) {
		ItemGroup group = null;
		for (ItemGroup group0 : ItemGroup.GROUPS) {
			if (group0.getId().equals(ID)) {
				group = group0;
			}
		}
		if (group == null) {
			throw new NullPointerException("No ItemGroup found for ID " + ID);
		}
		for (Item item : Registry.ITEM) {
			ItemGroup itemGroup = item.getGroup();
			if (itemGroup == group) {
				itemStacks.add(new ItemStack(item));
			}
		}
	}
	
}
