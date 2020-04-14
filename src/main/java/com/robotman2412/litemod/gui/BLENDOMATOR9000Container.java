package com.robotman2412.litemod.gui;

import com.robotman2412.litemod.foods.BLENDOMATOR9000BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class BLENDOMATOR9000Container extends Container {
	
	public BLENDOMATOR9000BlockEntity tileEnt;
	
	public BLENDOMATOR9000Container(int syncId, PlayerInventory playerInventory, PlayerEntity player, BLENDOMATOR9000BlockEntity ent) {
		super(null, syncId);
		tileEnt = ent;
		checkContainerSize(tileEnt, 5);
		tileEnt.onInvOpen(player);
		
		//BLENDOMATOR 9000! inventory
		addSlot(new Slot(tileEnt, 0, 71, 54) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return BLENDOMATOR9000BlockEntity.isFuel(stack);
			}
		});
		for (int i = 0; i < 3; i++) {
			addSlot(new Slot(tileEnt, i + 1, 48, 18 + 18 * i) {
				@Override
				public boolean canInsert(ItemStack stack) {
					return ent.isRecipeItem(stack);
				}
			});
		}
		addSlot(new Slot(tileEnt, 4, 112, 36) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}
		});
		
		//player inventory
		for (int i = 0 ; i < 9; i++) { //hotbar
			addSlot(new Slot(playerInventory, i, 8 + 18 * i, 142));
		}
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInventory, 9 + y * 9 + x, 8 + 18 * x, 84 + 18 * y));
			}
		}
		
	}
	
	@Override
	public boolean canUse(PlayerEntity player) {
		return this.tileEnt.canPlayerUseInv(player);
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int slot) {
		ItemStack slotStack = getSlot(slot).getStack();
		if (slot < 5) {
			//BLENDOMATOR 9000! inventory
			for (int i = 0; i < 9 * 4; i++) {
				tryFit(i + 5, slotStack);
			}
		}
		else
		{
			//player inventory
			if (tileEnt.isRecipeItem(slotStack)) {
				tryFit(1, slotStack);
				tryFit(2, slotStack);
				tryFit(3, slotStack);
			}
			if (BLENDOMATOR9000BlockEntity.isFuel(slotStack)) {
				tryFit(0, slotStack);
			}
		}
		return slotStack;
	}
	
	public int tryFit(int slot, ItemStack stack) {
		if (stack.isEmpty() || getSlot(slot) == null) {
			return 0;
		}
		ItemStack slotStack = getSlot(slot).getStack();
		if (slotStack.isEmpty()) {
			getSlot(slot).setStack(stack.copy());
			int put = stack.getCount();
			stack.decrement(put);
			return put;
		}
		else if (slotStack.getCount() + stack.getCount() < slotStack.getMaxCount() && slotStack.isItemEqual(stack)) {
			int put = Math.min(stack.getCount(), stack.getMaxCount() - slotStack.getCount());
			slotStack.increment(put);
			getSlot(slot).setStack(slotStack.copy());
			stack.decrement(put);
			return put;
		}
		else
		{
			return 0;
		}
	}
	
}
