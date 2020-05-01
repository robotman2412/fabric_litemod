package com.robotman2412.litemod.block.grave;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class TheStoneOfGravesBlockEntity extends BlockEntity implements Inventory, Tickable {
	
	public static final int DEFAULT_INVENTORY_SIZE = 50;
	
	public DefaultedList<ItemStack> theBigInventory;
	public UUID owner;
	public String lastKnownName;
	public String lastKnownOwnerName;
	
	public TheStoneOfGravesBlockEntity() {
		super(FabricLitemod.THE_STONE_OF_GRAVES_BLOCK_ENTITY);
		theBigInventory = DefaultedList.ofSize(DEFAULT_INVENTORY_SIZE, ItemStack.EMPTY);
	}
	
	public void slurpUpItems(Inventory other) {
		clear();
		for (int i = 0; i < other.getInvSize(); i++) {
			setInvStack(i, other.getInvStack(i).copy());
		}
	}
	
	public void gibvAllYouCan(Inventory other) {
		outer:
		for (int i = 0; i < getInvSize(); i++) {
			ItemStack myStack = getInvStack(i);
			for (int l = 0; l < 37; l++) {
				if (l == 36) {
					l += 4;
				}
				if (myStack.isEmpty()) {
					continue outer;
				}
				ItemStack theirStack = other.getInvStack(l);
				if (theirStack.isEmpty()) {
					other.setInvStack(l, myStack.copy());
					myStack.setCount(0);
				}
				else if (theirStack.isItemEqual(myStack)) {
					int maxCount = myStack.getMaxCount();
					int put = Math.min(myStack.getCount(), maxCount - theirStack.getCount());
					myStack.decrement(put);
					theirStack.increment(put);
				}
			}
		}
	}
	
	protected int stupidPacketSendingCounter;
	
	@Override
	public void tick() {
		if (world instanceof ServerWorld) {
			stupidPacketSendingCounter++;
			if (stupidPacketSendingCounter >= 40) {
				stupidPacketSendingCounter = 0;
				ServerWorld world = (ServerWorld) this.world;
				for (ServerPlayerEntity plaey : world.getPlayers()) {
					//the fuck is this shit
					if (pos.getSquaredDistance(plaey.getBlockPos()) < 30 * 30) {
						plaey.networkHandler.sendPacket(toUpdatePacket());
					}
				}
			}
		}
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		Inventories.toTag(tag, theBigInventory);
		if (owner == null) {
			owner = new UUID(0, 0);
		}
		if (lastKnownName == null) {
			lastKnownName = "";
		}
		if (lastKnownOwnerName == null) {
			tag.putString("lastKnownOwnerName", "");
		}
		else
		{
			tag.putString("lastKnownOwnerName", lastKnownOwnerName);
		}
		tag.putLong("ownerUuidMost", owner.getMostSignificantBits());
		tag.putLong("ownerUuidLeast", owner.getLeastSignificantBits());
		tag.putString("lastKnownName", lastKnownName);
		return tag;
	}
	
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, Registry.BLOCK_ENTITY_TYPE.getRawId(getType()), toTag(new CompoundTag()));
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		Inventories.fromTag(tag, theBigInventory);
		long least = tag.getLong("ownerUuidLeast");
		long most = tag.getLong("ownerUuidMost");
		owner = new UUID(most, least);
		lastKnownName = tag.getString("lastKnownName");
		lastKnownOwnerName = tag.getString("lastKnownOwnerName");
		if (lastKnownOwnerName.length() < 1) {
			lastKnownOwnerName = null;
		}
	}
	
	@Override
	public int getInvSize() {
		return theBigInventory.size();
	}
	
	@Override
	public boolean isInvEmpty() {
		return false;
	}
	
	@Override
	public ItemStack getInvStack(int slot) {
		return theBigInventory.get(slot);
	}
	
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack slotStack = getInvStack(slot);
		int taken = Math.min(amount, slotStack.getCount());
		ItemStack stacc = slotStack.copy();
		stacc.setCount(taken);
		slotStack.decrement(taken);
		return stacc;
	}
	
	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack slotStack = getInvStack(slot);
		theBigInventory.set(slot, ItemStack.EMPTY);
		return slotStack;
	}
	
	@Override
	public void setInvStack(int slot, ItemStack stack) {
		theBigInventory.set(slot, stack);
	}
	
	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return false;
	}
	
	@Override
	public void clear() {
		theBigInventory.clear();
	}
	
}
