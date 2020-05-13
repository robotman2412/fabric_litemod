package com.robotman2412.litemod.block.itemduct.part;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.itemduct.AbstractItemductBlockEntity;
import com.robotman2412.litemod.block.itemduct.ItemDuctItem;
import com.robotman2412.litemod.block.itemduct.ItemDuctPart;
import com.robotman2412.litemod.block.itemduct.ItemDuctPartType;
import com.robotman2412.litemod.block.itemduct.util.ConnectedInventory;
import com.robotman2412.litemod.block.itemduct.util.Itemducts;
import com.robotman2412.litemod.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class GoldenItemductPart extends ItemDuctPart<GoldenItemductPart> {
	
	public static ItemDuctPartType<GoldenItemductPart> TYPE = new ItemDuctPartType<>(GoldenItemductPart::new, new Identifier(FabricLitemod.MOD_ID, "golden_itemduct"));
	
	public GoldenItemductPart() {
		super(TYPE);
	}
	
	@Override
	public Object getRender(AbstractItemductBlockEntity itemduct) {
		return null;
	}
	
	@Override
	public boolean canInsert(ItemDuctItem item) {
		if (targetBlockEntity instanceof HopperBlockEntity && direction != Direction.UP) {
			BlockState target = entity.getWorld().getBlockState(targetPos);
			return target.get(HopperBlock.FACING) != direction.getOpposite();
		}
		return true;
	}
	
	@Override
	public ItemStack extraction() {
		if (targetBlockEntity instanceof HopperBlockEntity && direction != Direction.UP) {
			BlockState target = entity.getWorld().getBlockState(targetPos);
			List<ConnectedInventory> inventories = prepFit();
			if (target.get(HopperBlock.FACING) == direction.getOpposite()) {
				return simpleExtraction((stack) -> canFindFit(stack, inventories));
			}
		}
		return null;
	}
	
	public boolean canFindFit(ItemStack stack, List<ConnectedInventory> inventories) {
		for (ConnectedInventory inventory : inventories) {
			if (Utils.willItemFit(stack, inventory.inventory, inventory.directionFromItemduct.getOpposite())) {
				return true;
			}
		}
		return false;
	}
	
	public List<ConnectedInventory> prepFit() {
		List<ConnectedInventory> blockEntities = Itemducts.getAllConnectedInventories(entity);
		List<ConnectedInventory> inventories = new ArrayList<>(blockEntities.size() - 1);
		for (ConnectedInventory entity : blockEntities) {
			if (entity.blockEntity != targetBlockEntity) {
				inventories.add(entity);
			}
		}
		return inventories;
	}
	
}
