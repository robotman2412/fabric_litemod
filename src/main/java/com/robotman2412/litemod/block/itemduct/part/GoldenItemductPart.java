package com.robotman2412.litemod.block.itemduct.part;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.itemduct.AbstractItemductBlockEntity;
import com.robotman2412.litemod.block.itemduct.ItemDuctItem;
import com.robotman2412.litemod.block.itemduct.ItemDuctPart;
import com.robotman2412.litemod.block.itemduct.ItemDuctPartType;
import net.minecraft.util.Identifier;

public class GoldenItemductPart extends ItemDuctPart<GoldenItemductPart> {
	
	public static ItemDuctPartType<GoldenItemductPart> TYPE = new ItemDuctPartType<>(GoldenItemductPart::new, new Identifier(FabricLitemod.MOD_ID, "golden_itemduct"));
	
	protected GoldenItemductPart() {
		super(TYPE);
	}
	
	@Override
	public Object getRender(AbstractItemductBlockEntity itemduct) {
		return null;
	}
	
	@Override
	public boolean canInsert(ItemDuctItem item) {
		return true;
	}
	
}
