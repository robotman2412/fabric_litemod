package com.robotman2412.litemod.block.itemduct;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.itemduct.part.GoldenItemductPart;

public class GoldenItemductBlockEntity extends AbstractItemductBlockEntity {
	
	public GoldenItemductBlockEntity() {
		super(FabricLitemod.GOLDEN_ITEMDUCT_BLOCK_ENTITY);
	}
	
	@Override
	public ItemDuctPartType getDefaultPart() {
		return GoldenItemductPart.TYPE;
	}
	
}
