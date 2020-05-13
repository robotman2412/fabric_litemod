package com.robotman2412.litemod.block.itemduct.util;

import com.robotman2412.litemod.block.itemduct.AbstractItemductBlockEntity;
import com.robotman2412.litemod.block.itemduct.ItemDuctItem;
import com.robotman2412.litemod.block.itemduct.ItemDuctPart;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class Itemducts {
	
	public static int getMaxPathIterations() {
		return 500;
	}
	
	public static void pathFind(AbstractItemductBlockEntity itemduct, ItemDuctItem item) {
		World world = itemduct.getWorld();
		BlockPos dest = findDestination(itemduct, item);
		List<PathBit> pathBits = new LinkedList<>();
		pathBits.add(new PathBit(itemduct.getPos(), itemduct, null, null));
		int maxIterations = getMaxPathIterations();
		for (int i = 0; i < maxIterations; i++) {
			List<PathBit> newPathBits = new LinkedList<>();
			for (PathBit pathBit : pathBits) {
				for (Direction direction : Direction.values()) {
					if (direction.getOpposite() != pathBit.directionFromPrevious) {
						BlockPos outPos = pathBit.pos.offset(direction);
						if (!world.isChunkLoaded(outPos)) {
							continue;
						}
						BlockEntity blockEntity = world.getBlockEntity(outPos);
						PathBit path = new PathBit(outPos, blockEntity, direction, pathBit);
						if (outPos.equals(dest)) {
							setPath(path, item);
							return;
						}
						else if (blockEntity instanceof AbstractItemductBlockEntity) {
							newPathBits.add(path);
						}
					}
				}
			}
			pathBits = newPathBits;
		}
	}
	
	public static void setPath(PathBit pathBit, ItemDuctItem item) {
		item.path = new ItemDuctItemPath(pathBit);
	}
	
	public static BlockPos findDestination(AbstractItemductBlockEntity itemduct, ItemDuctItem item) {
		List<AbstractItemductBlockEntity> ducts = getAllConnectedDucts(new LinkedList<>(), itemduct);
		BlockPos pos = findDestination(ducts, itemduct, item, itemduct::isDesiredDestination);
		if (pos != null) {
			return pos;
		}
		else
		{
			return findDestination(ducts, itemduct, item, itemduct::isValidDestination);
		}
	}
	
	public static BlockPos findDestination(List<AbstractItemductBlockEntity> ducts, AbstractItemductBlockEntity itemduct, ItemDuctItem item, ItemductPredicate predicate) {
		BlockPos closestPos = null;
		double closestSqrDist = Double.POSITIVE_INFINITY;
		for (AbstractItemductBlockEntity duct : ducts) {
			for (Direction direction : Direction.values()) {
				if (duct == itemduct && direction == item.direction) {
					continue;
				}
				ItemDuctPart part = duct.getPart(direction);
				if (part != null && part.targetBlockEntity instanceof Inventory) {
					double dist = itemduct.getPos().getSquaredDistance(part.targetPos);
					if (dist < closestSqrDist && predicate.test(part.targetBlockEntity, item.stack, direction)) {
						closestPos = part.targetPos;
						closestSqrDist = dist;
					}
				}
			}
		}
		return closestPos;
	}
	
	public static List<AbstractItemductBlockEntity> getAllConnectedDucts(List<AbstractItemductBlockEntity> list, AbstractItemductBlockEntity itemduct) {
		if (list.contains(itemduct)) {
			return list;
		}
		list.add(itemduct);
		for (Direction direction : Direction.values()) {
			ItemDuctPart part = itemduct.getPart(direction);
			if (part != null && part.targetBlockEntity instanceof AbstractItemductBlockEntity) {
				getAllConnectedDucts(list, (AbstractItemductBlockEntity) part.targetBlockEntity);
			}
		}
		return list;
	}
	
	public static List<ConnectedInventory> getAllConnectedInventories(AbstractItemductBlockEntity itemduct) {
		List<AbstractItemductBlockEntity> ducts = getAllConnectedDucts(new LinkedList<>(), itemduct);
		List<ConnectedInventory> list = new LinkedList<>();
		for (AbstractItemductBlockEntity duct : ducts) {
			for (Direction direction : Direction.values()) {
				ItemDuctPart part = duct.getPart(direction);
				if (part != null && part.targetBlockEntity instanceof Inventory && !containsConnection(list, part)) {
					ConnectedInventory connection = new ConnectedInventory();
					connection.directionFromItemduct = direction;
					connection.part = part;
					connection.fromItemduct = duct;
					connection.inventory = (Inventory) part.targetBlockEntity;
					connection.blockEntity = part.targetBlockEntity;
					list.add(connection);
				}
			}
		}
		return list;
	}
	
	public static boolean containsConnection(List<ConnectedInventory> list, ItemDuctPart part) {
		for (ConnectedInventory inventory : list) {
			if (inventory.part == part) {
				return true;
			}
		}
		return false;
	}
	
}
