package com.robotman2412.litemod.foods;

import com.robotman2412.litemod.CountedIngredient;
import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.gui.BLENDOMATOR9000Container;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.LinkedList;
import java.util.List;

public class BLENDOMATOR9000BlockEntity extends BlockEntity implements Tickable, SidedInventory, ContainerFactory {
	
	public DefaultedList<ItemStack> inventory;
	public List<ItemStack> blendedItems;
	public int progress;
	public int maxProgress;
	public int fuelLeft;
	public int maxFuelLeft;
	public BlenderRecipe nowBlending;
	public BlenderRecipe availableRecipe;
	
	public BLENDOMATOR9000BlockEntity() {
		super(FabricLitemod.BLENDOMATOR9000_BLOCK_ENTITY);
		inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
		blendedItems = new LinkedList<>();
		maxFuelLeft = 1;
		maxProgress = 1;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		Inventories.toTag(tag, inventory);
		tag.putInt("max_fuel_left", maxFuelLeft);
		tag.putInt("fuel_left", fuelLeft);
		tag.putInt("max_progress", maxProgress);
		tag.putInt("progress", progress);
		if (nowBlending != null) {
			tag.putString("now_blending", nowBlending.getId().toString());
		}
		else
		{
			tag.putString("now_blending", "null");
		}
		return tag;
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		Inventories.fromTag(tag, inventory);
		String blendingId = tag.getString("now_blending");
		if (blendingId.equals("null")) {
			nowBlending = null;
		}
		else
		{
			Recipe<?> recipe = world.getRecipeManager().get(new Identifier(blendingId)).orElse(null);
			if (recipe instanceof BlenderRecipe) {
				nowBlending = (BlenderRecipe) recipe;
			}
			else
			{
				nowBlending = null;
			}
		}
		updateRecipe();
	}
	
	public void updateRecipe() {
		if (world != null) {
			availableRecipe = world.getRecipeManager().getFirstMatch(FabricLitemod.BLENDER_RECIPE_TYPE, this, world).orElse(null);
		}
	}
	
	@Override
	public void tick() {
		if (fuelLeft > 0) {
			fuelLeft -= Math.min(fuelLeft, 4);
			markDirty();
		}
		if (fuelLeft > 1 && progress < maxProgress && maxProgress > 1) {
			progress ++;
			if (progress >= maxProgress) {
				maxProgress = 1;
				progress = 0;
				nowBlending.consume(this);
				if (!world.isClient()) {
					ItemStack outStack = getInvStack(4);
					if (outStack.isEmpty()) {
						setInvStack(4, nowBlending.getOutput());
					} else {
						outStack.increment(nowBlending.getOutput().getCount());
					}
				}
				nowBlending = null;
			}
			markDirty();
		}
		if (availableRecipe != nowBlending) {
			progress = 0;
			maxProgress = 1;
			nowBlending = null;
			markDirty();
		}
		if (availableRecipe != null && nowBlending == null && fuelLeft > 0) {
			ItemStack outSlot = getInvStack(4);
			ItemStack outStack = availableRecipe.getOutput();
			if (outSlot.isEmpty() || (outSlot.isItemEqual(outStack) && outSlot.getCount() + outStack.getCount() < outSlot.getMaxCount())) {
				blendedItems.clear(); //reset to original value
				nowBlending = availableRecipe;
				maxProgress = availableRecipe.blendTicks;
				markDirty();
			}
		}
		if (availableRecipe != null) {
			if (fuelLeft == 0) {
				ItemStack fuelStack = getInvStack(0);
				if (!fuelStack.isEmpty()) {
					maxFuelLeft = fuelLeft = FuelRegistry.INSTANCE.get(fuelStack.getItem());
					Item remainder = fuelStack.getItem().getRecipeRemainder();
					if (remainder != null) {
						setInvStack(0, new ItemStack(remainder));
					}
					else
					{
						fuelStack.decrement(1);
					}
				}
				markDirty();
			}
		}
	}
	
	
	public boolean isRecipeItem(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		for (Recipe<?> recipe : world.getRecipeManager().values()) {
			if (recipe instanceof BlenderRecipe) {
				for (CountedIngredient ingredient : ((BlenderRecipe) recipe).ingredients) {
					if (ingredient.isApplicable(stack)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static boolean isFuel(ItemStack stack) {
		Integer stuff = FuelRegistry.INSTANCE.get(stack.getItem());
		return stuff != null && stuff != 0;
	}
	
	//region inventory
	@Override
	public int[] getInvAvailableSlots(Direction side) {
		return new int[] {
				0, 1, 2, 3, 4
		};
	}
	
	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
		switch (dir) {
			default:
				return slot == 0 && isFuel(stack);
			case UP:
				return slot >= 1 && slot <= 3 && isRecipeItem(stack);
			case DOWN:
				return false;
		}
	}
	
	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN) {
			return (slot == 0 && !isFuel(stack)) || slot == 4 || (slot >= 1 && slot <= 3 && !isRecipeItem(stack));
		}
		else if (dir == Direction.UP) {
			return false;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int getInvSize() {
		return inventory.size();
	}
	
	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public ItemStack getInvStack(int slot) {
		return inventory.get(slot);
	}
	
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack stack = inventory.get(slot);
		int remaining = stack.getCount();
		int taken = Math.min(remaining, amount);
		ItemStack copy = stack.copy();
		copy.setCount(taken);
		stack.setCount(stack.getCount() - taken);
		if (stack.getCount() == 0) {
			inventory.set(slot, ItemStack.EMPTY);
		}
		markDirty();
		updateRecipe();
		return copy;
	}
	
	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack stack = inventory.get(slot);
		inventory.set(slot, ItemStack.EMPTY);
		markDirty();
		updateRecipe();
		return stack;
	}
	
	@Override
	public void setInvStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		updateRecipe();
		markDirty();
	}
	
	@Override
	@SuppressWarnings("all")
	public boolean canPlayerUseInv(PlayerEntity player) {
		if (this.world.getBlockEntity(this.pos) != this) {
			return false;
		} else {
			return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}
	
	@Override
	public void clear() {
		inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
		markDirty();
	}
	
	@Override
	public Container createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new BLENDOMATOR9000Container(syncId, inv, player, this);
	}
	//endregion inventory
	
}
