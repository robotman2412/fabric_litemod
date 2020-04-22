package com.robotman2412.litemod.block;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class BlockWrapper extends Block {
	
	public final String blockName;
	public final boolean doBlockItem;
	
	public BlockWrapper(Settings settings, String blockName, boolean doBlockItem) {
		super(settings);
		this.blockName = blockName;
		this.doBlockItem = doBlockItem;
	}
	
	public RenderLayer getRenderLayer() {
		return RenderLayer.getSolid();
	}
	
	public Item.Settings getItemSettings() {
		return new Item.Settings();
	}
	
	public Identifier getIdentifier() {
		return new Identifier(FabricLitemod.MOD_ID, blockName);
	}
	
	public BlockItem getBlockItem() {
		return new BlockItem(this, getItemSettings());
	}
	
}
