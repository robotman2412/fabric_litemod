package com.robotman2412.litemod.block.itemduct;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractItemductBlockEntity extends BlockEntity implements Tickable {
	
	public ItemDuctPart up;
	public ItemDuctPart down;
	public ItemDuctPart north;
	public ItemDuctPart east;
	public ItemDuctPart south;
	public ItemDuctPart west;
	
	public List<ItemDuctItem> items;
	public boolean doSendUpdate;
	
	public AbstractItemductBlockEntity(BlockEntityType<?> type) {
		super(type);
		items = new LinkedList<>();
	}
	
	public abstract ItemDuctPartType getDefaultPart();
	
	public int getItemSpeed() {
		return 1;
	}
	
	public boolean wouldConnectToBlock(Direction direction) {
		if (world == null) {
			return false;
		}
		BlockPos pos = getPos().offset(direction);
		BlockState state = world.getBlockState(pos);
		BlockEntity entity = world.getBlockEntity(pos);
		return wouldConnectToBlock(pos, direction, state, entity);
	}
	
	public boolean wouldConnectToBlock(BlockPos pos, Direction relativeDirection, BlockState otherState, BlockEntity otherEntity) {
		return otherEntity instanceof AbstractItemductBlockEntity || otherEntity instanceof Inventory;
	}
	
	public void retryConnections() {
		boolean b = retryConnectionDir(Direction.UP);
		b |=		retryConnectionDir(Direction.DOWN);
		b |=		retryConnectionDir(Direction.NORTH);
		b |=		retryConnectionDir(Direction.EAST);
		b |=		retryConnectionDir(Direction.SOUTH);
		b |=		retryConnectionDir(Direction.WEST);
		if (b) {
			BlockState state = world.getBlockState(pos);
			world.setBlockState(pos, state
					.with(AbstractItemductBlock.UP, up != null)
					.with(AbstractItemductBlock.DOWN, down != null)
					.with(AbstractItemductBlock.NORTH, north != null)
					.with(AbstractItemductBlock.EAST, east != null)
					.with(AbstractItemductBlock.SOUTH, south != null)
					.with(AbstractItemductBlock.WEST, west != null)
			);
		}
	}
	
	public boolean retryConnectionDir(Direction direction) {
		ItemDuctPart part = getPart(direction);
		if (!wouldConnectToBlock(direction) && part != null && part.type == getDefaultPart()) {
			setPart(direction, null);
			part.onRemoved();
			return true;
		}
		if (wouldConnectToBlock(direction) && part == null) {
			setPart(direction, getDefaultPart());
			return true;
		}
		return false;
	}
	
	public void setPart(Direction direction, ItemDuctPartType partType) {
		ItemDuctPart part = partType == null ? null : partType.initialise(this, pos, direction);
		switch (direction) {
			default:
				throw new NullPointerException("Input direction is null.");
			case DOWN:
				down = part;
				break;
			case UP:
				up = part;
				break;
			case NORTH:
				north = part;
				break;
			case EAST:
				east = part;
				break;
			case SOUTH:
				south = part;
				break;
			case WEST:
				west = part;
				break;
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
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, Registry.BLOCK_ENTITY_TYPE.getRawId(getType()), toTag(new CompoundTag()));
	}
	
	@Override
	public void tick() {
		if ((world.getTime() & 127) == 0) {
			doSendUpdate = true;
		}
		
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
				if (dist <= 0) {
					dist = -dist;
					redirectItem(item);
					doSendUpdate = true;
				}
				item.distanceFromCenter = dist;
			}
			else
			{
				//ensure we don't infinitely send items into the distance
				switch (item.direction) {
					case UP:
						if (!canUp) {
							item.isTravelingToCenter = true;
							continue;
						}
						break;
					case DOWN:
						if (!canDown) {
							item.isTravelingToCenter = true;
							continue;
						}
						break;
					case NORTH:
						if (!canNorth) {
							item.isTravelingToCenter = true;
							continue;
						}
						break;
					case EAST:
						if (!canEast) {
							item.isTravelingToCenter = true;
							continue;
						}
						break;
					case SOUTH:
						if (!canSouth) {
							item.isTravelingToCenter = true;
							continue;
						}
						break;
					case WEST:
						if (!canWest) {
							item.isTravelingToCenter = true;
							continue;
						}
						break;
				}
				
				int dist = item.distanceFromCenter + getItemSpeed();
				if (dist >= 8) {
					ItemDuctPart part = getPart(item.direction);
					if (part != null && part.canInsert(item)) {
						BlockEntity otherEntity = part.targetBlockEntity;
						if (otherEntity instanceof AbstractItemductBlockEntity) {
							AbstractItemductBlockEntity duct = (AbstractItemductBlockEntity) otherEntity;
							ItemDuctPart receiver = duct.getPart(item.direction.getOpposite());
							if (receiver != null && receiver.canReceive(item)) {
								duct.recieve(item);
								removed.add(item);
								doSendUpdate = true;
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
									if (other.isEmpty()) {
										inv.setInvStack(slot, item.stack);
										item.stack = ItemStack.EMPTY;
									}
									else if (other.isItemEqual(item.stack)) {
										int max = item.stack.getMaxCount();
										int put = Math.min(item.stack.getCount(), max - other.getCount());
										other.increment(put);
										item.stack.decrement(put);
									}
								}
							}
						}
						else if (otherEntity instanceof Inventory) {
							Inventory inv = (Inventory) otherEntity;
							for (int slot = 0; slot < inv.getInvSize(); slot++) {
								if (item.stack.isEmpty()) {
									removed.add(item);
									break;
								}
								ItemStack other = inv.getInvStack(slot);
								if (other.isEmpty()) {
									inv.setInvStack(slot, item.stack);
									item.stack = ItemStack.EMPTY;
									if (item.stack.isEmpty()) {
										removed.add(item);
										break;
									}
								}
								else if (other.isItemEqual(item.stack)) {
									int max = item.stack.getMaxCount();
									int put = Math.min(item.stack.getCount(), max - other.getCount());
									other.increment(put);
									item.stack.decrement(put);
									if (item.stack.isEmpty()) {
										removed.add(item);
										break;
									}
								}
							}
						}
					}
					doSendUpdate = true;
					item.isTravelingToCenter = true;
					dist = 16 - dist;
				}
				item.distanceFromCenter = dist;
			}
		}
		
		for (ItemDuctItem item : removed) {
			items.remove(item);
		}
		
		retryConnections();
		if (doSendUpdate && world instanceof ServerWorld) {
			doSendUpdate = false;
			markDirty();
			ServerWorld world = (ServerWorld) this.world;
			for (ServerPlayerEntity plaey : world.getPlayers()) {
				//the fuck is this shit
				if (pos.getSquaredDistance(plaey.getBlockPos()) < 30 * 30) {
					plaey.networkHandler.sendPacket(toUpdatePacket());
				}
			}
		}
	}
	
	public abstract void itemExtracted(ItemDuctItem item);
	
	public abstract void redirectItem(ItemDuctItem item);
	
	/** Recieves an item from another itemduct, where the item of the other itemduct has not been changed to accommodate this one. */
	public void recieve(ItemDuctItem item) {
		Direction dir = item.direction;
		ItemStack stack = item.stack.copy();
		int dist = 16 - item.distanceFromCenter;
		ItemDuctItem myItem = new ItemDuctItem();
		myItem.direction = dir.getOpposite();
		myItem.distanceFromCenter = dist;
		myItem.stack = stack;
		myItem.isTravelingToCenter = true;
		myItem.path = item.path;
		item.path = null;
		items.add(myItem);
		if (!world.isClient()) {
			markDirty();
			ServerWorld world = (ServerWorld) this.world;
			for (ServerPlayerEntity plaey : world.getPlayers()) {
				//the fuck is this shit
				if (pos.getSquaredDistance(plaey.getBlockPos()) < 30 * 30) {
					plaey.networkHandler.sendPacket(toUpdatePacket());
				}
			}
		}
	}
	
	public boolean tickDirection(Direction direction, ItemDuctPart part) {
		if (part == null) {
			return false;
		}
		BlockPos otherBlock = getPos().offset(direction);
		part.targetBlockEntity = world.getBlockEntity(otherBlock);
		if (part.targetBlockEntity != null) {
			ItemStack stack = part.extractionTick();
			if (stack != null && !world.isClient()) {
				ItemDuctItem item = new ItemDuctItem();
				item.stack = stack;
				item.isTravelingToCenter = true;
				item.distanceFromCenter = 9;
				item.direction = direction;
				items.add(item);
				itemExtracted(item);
				doSendUpdate = true;
			}
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
		if (tag.get("items") instanceof ListTag) {
			for (Tag item : (ListTag) tag.get("items")) {
				if (item instanceof CompoundTag) {
					items.add(ItemDuctItem.fromTag((CompoundTag) item));
				}
			}
		}
		tag = tag.getCompound("connections");
		if (tag.contains("up")) up = AbstractItemductBlock.loadPart(tag.getCompound("up"), this, Direction.UP);
		if (tag.contains("down")) down = AbstractItemductBlock.loadPart(tag.getCompound("down"), this, Direction.DOWN);
		if (tag.contains("north")) north = AbstractItemductBlock.loadPart(tag.getCompound("north"), this, Direction.NORTH);
		if (tag.contains("east")) east = AbstractItemductBlock.loadPart(tag.getCompound("east"), this, Direction.EAST);
		if (tag.contains("south")) south = AbstractItemductBlock.loadPart(tag.getCompound("south"), this, Direction.SOUTH);
		if (tag.contains("west")) west = AbstractItemductBlock.loadPart(tag.getCompound("west"), this, Direction.WEST);
		retryConnections();
	}
	
	public boolean isValidDestination(BlockEntity targetBlockEntity, ItemStack stack, Direction fromDirection) {
		return true;
	}
	
	public boolean isDesiredDestination(BlockEntity targetBlockEntity, ItemStack stack, Direction fromDirection) {
		return true;
	}
}
