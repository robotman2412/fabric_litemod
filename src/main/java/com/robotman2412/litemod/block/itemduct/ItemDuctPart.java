package com.robotman2412.litemod.block.itemduct;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Predicate;

public abstract class ItemDuctPart<Type extends ItemDuctPart> {
	
	public final ItemDuctPartType<Type> type;
	public BlockPos targetPos;
	public BlockEntity targetBlockEntity;
	public AbstractItemductBlockEntity entity;
	public int extractionTimer;
	public Direction direction;
	
	protected ItemDuctPart(ItemDuctPartType<Type> type) {
		this.type = type;
	}
	
	public Identifier getID() {
		return type.getID();
	}
	
	/**
	 * Specifies every extraction tick time.
	 * 1 is every tick.
	 * <1 is not at all.
	 * @return the amount of ticks to wait +1
	 */
	public int extractionTickEvery() {
		return 2;
	}
	
	public final ItemStack extractionTick() {
		int tick = extractionTickEvery();
		if (tick < 1) {
			return null;
		}
		extractionTimer ++;
		if (extractionTimer >= tick) {
			extractionTimer = 0;
			return extraction();
		}
		return null;
	}
	
	/**
	 * Item may be pulled into the current itemduct.
	 * @return the item pulled in
	 */
	public ItemStack extraction() {
		return null;
	}
	
	protected ItemStack simpleExtraction(Predicate<ItemStack> predicate) {
		if (targetBlockEntity instanceof SidedInventory) {
			SidedInventory inventory = (SidedInventory) targetBlockEntity;
			int[] slots = inventory.getInvAvailableSlots(direction.getOpposite());
			for (int i : slots) {
				ItemStack stack = inventory.getInvStack(i);
				if (!stack.isEmpty() && (predicate == null || predicate.test(stack))) {
					inventory.setInvStack(i, ItemStack.EMPTY);
					return stack;
				}
			}
		}
		else if (targetBlockEntity instanceof Inventory) {
			Inventory inventory = (Inventory) targetBlockEntity;
			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack stack = inventory.getInvStack(i);
				if (!stack.isEmpty() && (predicate == null || predicate.test(stack))) {
					inventory.setInvStack(i, ItemStack.EMPTY);
					return stack;
				}
			}
		}
		return null;
	}
	
	/**
	 * Should return false when the item would be immediately extracted.
	 * @return whether an item can go out of the current itemduct
	 * @param item the item to filter
	 */
	public abstract boolean canInsert(ItemDuctItem item);
	
	public abstract Object getRender(AbstractItemductBlockEntity itemduct); //TODO: create a renderer type
	
	public CompoundTag toTag(CompoundTag tagIn) {
		tagIn.putString("type", getID().toString());
		return tagIn;
	}
	
	public void fromTag(CompoundTag tagIn) {
		
	}
	
	public void onRemoved() {
		
	}
	
	public boolean canReceive(ItemDuctItem item) {
		return true;
	}
	
}
