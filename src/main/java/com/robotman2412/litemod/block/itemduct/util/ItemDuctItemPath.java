package com.robotman2412.litemod.block.itemduct.util;

import javafx.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.LinkedList;
import java.util.List;

public class ItemDuctItemPath {
	
	public List<Pair<BlockPos, Direction>> directions;
	
	public ItemDuctItemPath(PathBit path) {
		Direction going = null;
		List<PathBit> pathBitList = new LinkedList<>();
		directions = new LinkedList<>();
		for (; path != null; path = path.previous) {
			pathBitList.add(0, path);
		}
		for (PathBit pathBit : pathBitList) {
			if (pathBit.directionFromPrevious != going) {
				going = pathBit.directionFromPrevious;
				directions.add(new Pair<>(pathBit.previous.pos, going));
			}
		}
	}
	
	public ItemDuctItemPath() {
		
	}
	
	/**
	 * Gets and then removes the direction to go at the given block.
	 * @return the direction to turn at the given block, or null if to ccontinue going straight
	 */
	public Direction getDirectionAtBlock(BlockPos pos) {
		int indexial = -1;
		for (int i = 0; i < directions.size(); i++) {
			Pair<BlockPos, Direction> directionPair = directions.get(i);
			if (directionPair.getKey().equals(pos)) {
				indexial = i;
				break;
			}
		}
		if (indexial == -1) {
			return null;
		}
		else
		{
			Direction direction = directions.get(indexial).getValue();
			directions.remove(indexial);
			return direction;
		}
	}
	
	/**
	 * Gets and but does not remove the direction to go at the given block.
	 * @return the direction to turn at the given block, or null if to ccontinue going straight
	 */
	public Direction peekDirectionAtBlock(BlockPos pos) {
		for (int i = 0; i < directions.size(); i++) {
			Pair<BlockPos, Direction> directionPair = directions.get(i);
			if (directionPair.getKey().equals(pos)) {
				return directionPair.getValue();
			}
		}
		return null;
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (Pair<BlockPos, Direction> directionPair : directions) {
			CompoundTag directionTag = new CompoundTag();
			directionTag.put("pos", posToTag(directionPair.getKey()));
			directionTag.putString("direction", directionPair.getValue().getName());
			list.add(directionTag);
		}
		tag.put("directions", list);
		return tag;
	}
	
	public static BlockPos tagToPos(CompoundTag tag) {
		int x = tag.getInt("x");
		int y = tag.getInt("y");
		int z = tag.getInt("z");
		return new BlockPos(x, y, z);
	}
	
	public static Tag posToTag(BlockPos pos) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
		return tag;
	}
	
	public static ItemDuctItemPath fromTag(CompoundTag tag) {
		if (tag.get("directions") instanceof ListTag) {
			List<Pair<BlockPos, Direction>> directions = new LinkedList<>();
			for (Tag t : (ListTag) tag.get("directions")) {
				CompoundTag directionPairTag = (CompoundTag) t;
				CompoundTag posTag = directionPairTag.getCompound("pos");
				int x = posTag.getInt("x");
				int y = posTag.getInt("y");
				int z = posTag.getInt("z");
				BlockPos pos = new BlockPos(x, y, z);
				Direction direction = Direction.byName(directionPairTag.getString("direction"));
				directions.add(new Pair<>(pos, direction));
			}
			ItemDuctItemPath path = new ItemDuctItemPath();
			path.directions = directions;
			return path;
		}
		return null;
	}
	
}
