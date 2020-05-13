package com.robotman2412.litemod.block.itemduct.util;

import com.robotman2412.litemod.block.itemduct.AbstractItemductBlockEntity;
import com.robotman2412.litemod.block.itemduct.ItemDuctPart;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;

public class ConnectedInventory {
	
	public Inventory inventory;
	public BlockEntity blockEntity;
	public AbstractItemductBlockEntity fromItemduct;
	public Direction directionFromItemduct;
	public ItemDuctPart part;
	
}
