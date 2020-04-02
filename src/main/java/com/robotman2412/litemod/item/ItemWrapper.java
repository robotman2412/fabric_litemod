package com.robotman2412.litemod.item;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemWrapper extends Item {
	
	public final String itemName;
	
	public ItemWrapper(Settings settings, String itemName) {
		super(settings);
		this.itemName = itemName;
	}
	
	public Identifier getIdentifier() {
		return new Identifier(FabricLitemod.MOD_ID, itemName);
	}
	
}
