package com.robotman2412.litemod.item;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemWrapper extends Item {
	
	public Identifier id;
	
	public ItemWrapper(Settings settings, String itemName) {
		super(settings);
		this.id = new Identifier(FabricLitemod.MOD_ID, itemName);
	}
	
	public ItemWrapper setID(Identifier id) {
		this.id = id;
		return this;
	}
	
	public Identifier getIdentifier() {
		return id;
	}
	
}
