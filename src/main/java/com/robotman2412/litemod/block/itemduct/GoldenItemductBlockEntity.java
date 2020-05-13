package com.robotman2412.litemod.block.itemduct;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.itemduct.part.GoldenItemductPart;
import com.robotman2412.litemod.block.itemduct.util.Itemducts;
import com.robotman2412.litemod.util.Utils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class GoldenItemductBlockEntity extends AbstractItemductBlockEntity {
	
	public GoldenItemductBlockEntity() {
		super(FabricLitemod.GOLDEN_ITEMDUCT_BLOCK_ENTITY);
	}
	
	@Override
	public ItemDuctPartType getDefaultPart() {
		return GoldenItemductPart.TYPE;
	}
	
	@Override
	public void itemExtracted(ItemDuctItem item) {
		Itemducts.pathFind(this, item);
	}
	
	@Override
	public void redirectItem(ItemDuctItem item) {
		if (item.path != null) {
			Direction direction = item.path.getDirectionAtBlock(getPos());
			if (direction == null) {
				direction = item.direction.getOpposite();
			}
			item.direction = direction;
			item.isTravelingToCenter = false;
		}
	}
	
	@Override
	public boolean isValidDestination(BlockEntity targetBlockEntity, ItemStack stack, Direction fromDirection) {
		return Utils.willItemFit(stack, (Inventory) targetBlockEntity, fromDirection.getOpposite());
	}
	
	@Override
	public boolean isDesiredDestination(BlockEntity targetBlockEntity, ItemStack stack, Direction fromDirection) {
		return Utils.willItemFitInExisting(stack, (Inventory) targetBlockEntity, fromDirection.getOpposite());
	}
	
}
