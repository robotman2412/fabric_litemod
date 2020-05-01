package com.robotman2412.litemod.block.itemduct;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class ItemDuctPart<Type extends ItemDuctPart> {
	
	public final ItemDuctPartType<Type> type;
	public BlockPos targetPos;
	public BlockEntity targetBlockEntity;
	public AbstractItemductBlockEntity entity;
	public int extractionTimer;
	
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
	
	public final void extractionTick() {
		int tick = extractionTickEvery();
		if (tick < 1) {
			return;
		}
		extractionTimer ++;
		if (extractionTimer >= tick) {
			extractionTimer = 0;
			extraction();
		}
	}
	
	/**
	 * Item may be pulled into the current itemduct.
	 */
	public void extraction() {
		
	}
	
	/**
	 * Checked before the check if an item fits in the other block.
	 * @return whether an item can go out of the current itemduct
	 * @param item the item to filter
	 */
	public abstract boolean canInsert(ItemDuctItem item); //TODO: whatever arguments this takes
	
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
