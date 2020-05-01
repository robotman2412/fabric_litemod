package com.robotman2412.litemod.block.itemduct;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

public class ItemDuctItem {
	
	public ItemStack stack;
	public Direction direction;
	/** 8 is at the edge. */
	public int distanceFromCenter;
	public boolean isTravelingToCenter;
	
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("stack", stack.toTag(new CompoundTag()));
		tag.putInt("distanceFromCenter", distanceFromCenter);
		tag.putBoolean("isTravelingToCenter", isTravelingToCenter);
		tag.putString("direction", direction.toString());
		return tag;
	}
	
	public static ItemDuctItem fromTag(CompoundTag tag) {
		ItemDuctItem item = new ItemDuctItem();
		item.stack = ItemStack.fromTag(tag.getCompound("stack"));
		item.distanceFromCenter = tag.getInt("distanceFromCenter");
		item.isTravelingToCenter = tag.getBoolean("isTravelingToCenter");
		item.direction = Direction.byName(tag.getString("direction"));
		return item;
	}
	
}
