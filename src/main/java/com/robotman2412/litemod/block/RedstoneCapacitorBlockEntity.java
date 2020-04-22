package com.robotman2412.litemod.block;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.util.IRedstoneInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RedstoneCapacitorBlockEntity extends BlockEntity implements Tickable, IRedstoneInfo {
	
	public int powerLevel;
	public boolean isLocked;
	public int decrementTimer;
	public int powerLevelIn;
	
	public RedstoneCapacitorBlockEntity() {
		super(FabricLitemod.REDSTONE_CAPACITOR_BLOCK_ENTITY);
		decrementTimer = 0;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("power", powerLevel);
		tag.putBoolean("locked", isLocked);
		tag.putInt("decrement_in", decrementTimer);
		tag.putInt("power_in", powerLevelIn);
		return super.toTag(tag);
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		powerLevel = tag.getInt("power");
		isLocked = tag.getBoolean("locked");
		decrementTimer = tag.getInt("decrement_in");
		powerLevelIn = tag.getInt("power_in");
	}
	
	@Override
	public void tick() {
		boolean sendUpdate = false;
		decrementTimer ++;
		if (powerLevelIn > powerLevel) {
			powerLevel ++;
			sendUpdate = true;
		}
		if (decrementTimer >= 4) {
			decrementTimer = 0;
			if (!isLocked && powerLevel > powerLevelIn) {
				powerLevel = Math.max(powerLevel - 1, 0);
				sendUpdate = true;
			}
		}
		if (sendUpdate) {
			markDirty();
			BlockState state = world.getBlockState(getPos());
			Direction facing = state.get(Properties.HORIZONTAL_FACING);
			BlockPos frontPos = getPos().add(facing.getOffsetX(), 0, facing.getOffsetZ());
			BlockPos backPos = getPos().add(-facing.getOffsetX(), 0, -facing.getOffsetZ());
			BlockState front = world.getBlockState(frontPos);
			BlockState back = world.getBlockState(backPos);
			if (front.isSimpleFullBlock(world, frontPos)) {
				world.updateNeighbors(frontPos, front.getBlock());
			}
			if (back.isSimpleFullBlock(world, backPos)) {
				world.updateNeighbors(backPos, back.getBlock());
			}
			world.setBlockState(getPos(), state.with(RedstoneCapacitorBlock.POWER, RedstoneCapacitorBlock.PowerEnum.from(powerLevel)));
			world.updateNeighbors(getPos(), FabricLitemod.REDSTONE_CAPACITOR_BLOCK);
		}
	}
	
	@Override
	public boolean isEmittedPower() {
		return true;
	}
	
	@Override
	public int getPowerLevel(BlockState state) {
		return powerLevel;
	}
	
}
