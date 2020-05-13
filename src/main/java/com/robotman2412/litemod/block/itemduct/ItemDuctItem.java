package com.robotman2412.litemod.block.itemduct;

import com.robotman2412.litemod.block.itemduct.util.ItemDuctItemPath;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

public class ItemDuctItem {
	
	public ItemStack stack;
	public Direction direction;
	/** 8 is at the edge. */
	public int distanceFromCenter;
	public boolean isTravelingToCenter;
	public ItemDuctItemPath path;
	
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("stack", stack.toTag(new CompoundTag()));
		tag.putInt("distanceFromCenter", distanceFromCenter);
		tag.putBoolean("isTravelingToCenter", isTravelingToCenter);
		tag.putString("direction", direction.toString());
		tag.putBoolean("has_path", path != null);
		if (path != null) {
			tag.put("path", path.toTag());
		}
		return tag;
	}
	
	public static ItemDuctItem fromTag(CompoundTag tag) {
		ItemDuctItem item = new ItemDuctItem();
		item.stack = ItemStack.fromTag(tag.getCompound("stack"));
		item.distanceFromCenter = tag.getInt("distanceFromCenter");
		item.isTravelingToCenter = tag.getBoolean("isTravelingToCenter");
		item.direction = Direction.byName(tag.getString("direction"));
		if (tag.getBoolean("has_path")) {
			item.path = ItemDuctItemPath.fromTag(tag.getCompound("path"));
		}
		return item;
	}
	
}
