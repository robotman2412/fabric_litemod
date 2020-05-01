package com.robotman2412.litemod.block.itemduct;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractItemductBlockEntity extends BlockEntity implements Tickable {
	
	protected ItemDuctPart up;
	protected ItemDuctPart down;
	protected ItemDuctPart north;
	protected ItemDuctPart east;
	protected ItemDuctPart south;
	protected ItemDuctPart west;
	
	protected List<ItemDuctItem> items;
	
	public AbstractItemductBlockEntity(BlockEntityType<?> type) {
		super(type);
		items = new LinkedList<>();
		ItemDuctItem itemi = new ItemDuctItem();
		itemi.direction = Direction.NORTH;
		itemi.isTravelingToCenter = true;
		itemi.distanceFromCenter = 4;
		itemi.stack = new ItemStack(Items.DAMAGED_ANVIL, 32);
		items.add(itemi);
	}
	
	public abstract ItemDuctPartType getDefaultPart();
	
	public int getItemSpeed() {
		return 1;
	}
	
	public boolean wouldConnectToBlock(Direction direction) {
		BlockPos pos = getPos().offset(direction);
		BlockState state = getWorld().getBlockState(pos);
		BlockEntity entity = getWorld().getBlockEntity(pos);
		return wouldConnectToBlock(pos, direction, state, entity);
	}
	
	public boolean wouldConnectToBlock(BlockPos pos, Direction relativeDirection, BlockState otherState, BlockEntity otherEntity) {
		return otherEntity instanceof Inventory;
	}
	
	public void retryConnections() {
		retryConnectionDir(Direction.UP);
		retryConnectionDir(Direction.DOWN);
		retryConnectionDir(Direction.NORTH);
		retryConnectionDir(Direction.EAST);
		retryConnectionDir(Direction.SOUTH);
		retryConnectionDir(Direction.WEST);
	}
	
	public void retryConnectionDir(Direction direction) {
		ItemDuctPart part = getPart(direction);
		if (!wouldConnectToBlock(direction) &&part != null && part.type == getDefaultPart()) {
			setPart(direction, null);
			part.onRemoved();
		}
		if (wouldConnectToBlock(direction) && part == null) {
			setPart(direction, getDefaultPart());
		}
	}
	
	public void setPart(Direction direction, ItemDuctPartType partType) {
		ItemDuctPart part = partType == null ? null : partType.initialise(this, pos, direction);
		switch (direction) {
			default:
				throw new NullPointerException("Input direction is null.");
			case DOWN:
				down = part;
			case UP:
				up = part;
			case NORTH:
				north = part;
			case EAST:
				east = part;
			case SOUTH:
				south = part;
			case WEST:
				west = part;
		}
	}
	
	public ItemDuctPart getPart(Direction direction) {
		switch (direction) {
			default:
				throw new NullPointerException("Input direction is null.");
			case DOWN:
				return down;
			case UP:
				return up;
			case NORTH:
				return north;
			case EAST:
				return east;
			case SOUTH:
				return south;
			case WEST:
				return west;
		}
	}
	
	@Override
	public void tick() {
		boolean canUp = tickDirection(Direction.UP, up);
		boolean canDown = tickDirection(Direction.DOWN, down);
		boolean canNorth = tickDirection(Direction.NORTH, north);
		boolean canEast = tickDirection(Direction.EAST, east);
		boolean canSouth = tickDirection(Direction.SOUTH, south);
		boolean canWest = tickDirection(Direction.WEST, west);
		
		List<ItemDuctItem> removed = new LinkedList<>();
		
		for (ItemDuctItem item : items) {
			if (item.isTravelingToCenter) {
				int dist = item.distanceFromCenter - getItemSpeed();
				if (dist < 0) {
					dist = -dist;
					item.direction = item.direction.getOpposite(); //TODO: redirection based on stuff
				}
				item.distanceFromCenter = dist;
			}
			else
			{
				//ensure we don't infinitely send items into the distance
				switch (item.direction) {
					case UP:
						if (!canUp) item.isTravelingToCenter = true;
						continue;
					case DOWN:
						if (!canDown) item.isTravelingToCenter = true;
						continue;
					case NORTH:
						if (!canNorth) item.isTravelingToCenter = true;
						continue;
					case EAST:
						if (!canEast) item.isTravelingToCenter = true;
						continue;
					case SOUTH:
						if (!canSouth) item.isTravelingToCenter = true;
						continue;
					case WEST:
						if (!canWest) item.isTravelingToCenter = true;
						continue;
				}
				
				int dist = item.distanceFromCenter + getItemSpeed();
				if (dist >= 8) {
					ItemDuctPart part = getPart(item.direction);
					if (part != null && part.canInsert(item)) {
						BlockEntity otherEntity = part.targetBlockEntity;
						if (otherEntity instanceof AbstractItemductBlockEntity) {
							AbstractItemductBlockEntity duct = (AbstractItemductBlockEntity) otherEntity;
							ItemDuctPart receiver = ((AbstractItemductBlockEntity) otherEntity).getPart(item.direction.getOpposite());
							if (receiver.canReceive(item)) {
								duct.recieve(item);
								removed.add(item);
							}
						}
						else if (otherEntity instanceof SidedInventory) {
							SidedInventory inv = (SidedInventory) otherEntity;
							Direction insertionDir = item.direction.getOpposite();
							int[] available = inv.getInvAvailableSlots(insertionDir);
							for (int slot : available) {
								if (item.stack.isEmpty()) {
									break;
								}
								if (inv.canInsertInvStack(slot, item.stack, insertionDir)) {
									ItemStack other = inv.getInvStack(slot);
									if (other.isItemEqual(item.stack) || other.isEmpty()) {
										int max = item.stack.getMaxCount();
										int put = Math.min(item.stack.getCount(), max - other.getCount());
										other.increment(put);
										item.stack.decrement(put);
										inv.setInvStack(slot, other);
									}
								}
							}
						}
						else if (otherEntity instanceof Inventory) {
							Inventory inv = (Inventory) otherEntity;
							for (int slot = 0; slot < inv.getInvSize(); slot++) {
								if (item.stack.isEmpty()) {
									break;
								}
								ItemStack other = inv.getInvStack(slot);
								if (other.isItemEqual(item.stack) || other.isEmpty()) {
									int max = item.stack.getMaxCount();
									int put = Math.min(item.stack.getCount(), max - other.getCount());
									other.increment(put);
									item.stack.decrement(put);
									inv.setInvStack(slot, other);
								}
							}
						}
					}
					item.isTravelingToCenter = true;
					dist = 16 - dist;
				}
				item.distanceFromCenter = dist;
			}
		}
		
		for (ItemDuctItem item : removed) {
			items.remove(item);
		}
	}
	
	/** Recieves an item from another itemduct, where the item of the other itemduct has not been changed to accommodate this one. */
	public void recieve(ItemDuctItem item) {
		Direction dir = item.direction;
		ItemStack stack = item.stack.copy();
		int dist = 16 - item.distanceFromCenter;
		ItemDuctItem myItem = new ItemDuctItem();
		myItem.direction = dir;
		myItem.distanceFromCenter = dist;
		myItem.stack = stack;
		myItem.isTravelingToCenter = true;
		items.add(myItem);
	}
	
	public boolean tickDirection(Direction direction, ItemDuctPart part) {
		if (part == null) {
			return false;
		}
		BlockPos otherBlock = getPos().offset(direction);
		if (part.targetBlockEntity != null && part.targetBlockEntity.isRemoved()) {
			part.targetBlockEntity = world.getBlockEntity(otherBlock);
		}
		if (part.targetBlockEntity != null) {
			part.extractionTick();
			return true;
		}
		return false;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		ListTag listable = new ListTag();
		for (ItemDuctItem item : items) {
			listable.add(item.toTag(new CompoundTag()));
		}
		tag.put("items", listable);
		CompoundTag connections = new CompoundTag();
		if (up != null) connections.put("up", up.toTag(new CompoundTag()));
		if (down != null) connections.put("down", down.toTag(new CompoundTag()));
		if (north != null) connections.put("north", north.toTag(new CompoundTag()));
		if (east != null) connections.put("east", east.toTag(new CompoundTag()));
		if (south != null) connections.put("south", south.toTag(new CompoundTag()));
		if (west != null) connections.put("west", west.toTag(new CompoundTag()));
		tag.put("connections", connections);
		return tag;
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		items.clear();
		for (Tag item : (ListTag) tag.get("items")) {
			if (item instanceof CompoundTag) {
				items.add(ItemDuctItem.fromTag((CompoundTag) item));
			}
		}
		tag = tag.getCompound("connections");
		if (tag.contains("up")) up = AbstractItemductBlock.loadPart(tag.getCompound("up"));
		if (tag.contains("down")) down = AbstractItemductBlock.loadPart(tag.getCompound("down"));
		if (tag.contains("north")) north = AbstractItemductBlock.loadPart(tag.getCompound("north"));
		if (tag.contains("east")) east = AbstractItemductBlock.loadPart(tag.getCompound("east"));
		if (tag.contains("south")) south = AbstractItemductBlock.loadPart(tag.getCompound("south"));
		if (tag.contains("west")) west = AbstractItemductBlock.loadPart(tag.getCompound("west"));
		retryConnections();
	}
	
}
