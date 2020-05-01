package com.robotman2412.litemod.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class Utils {
	
	public static int healthConfusion = 1;
	
	public static boolean isHeartUv(int u, int v) {
		//16, 0 -> 178, 8
		//52, 9 -> 123, 17
		//16, 45 -> 178, 53
		if (u >= 16 && u <= 178 && (v <= 8 || (v >= 45 && v <= 53))) {
				return true;
		}
		if (u >= 52 && u <= 123 && v <= 17) { //v >= 9 is always true
			return true;
		}
		return false;
	}
	
	public static boolean isEnchanted(ItemStack stack) {
		return stack.getEnchantments().size() > 0;
	}
	
	public static boolean isSlimeChunk(long seed, int x, int z) {
		Random rnd = new Random(seed +
				(long) (x * x * 0x4c1906) +
				(long) (x * 0x5ac0db) +
				(long) (z * z) * 0x4307a7L +
				(long) (z * 0x5f24f) ^ 0x3ad8025f);
		return rnd.nextInt(10) == 0;
	}
	
	public static boolean isSlimeChunk(IWorld world, Chunk chunk) {
		return isSlimeChunk(world.getSeed(), chunk.getPos().x, chunk.getPos().z);
	}
	
	public static BlockPos getClosestPosInChunk(ChunkPos chunk, BlockPos other) {
		int posX = other.getX();
		int posY = other.getY();
		int posZ = other.getZ();
		int minX = chunk.x * 16;
		int minZ = chunk.z * 16;
		int maxX = minX + 15;
		int maxZ = minZ + 15;
		
		if (posX < minX) {
			posX = minX;
		}
		else if (posX > maxX) {
			posX = maxX;
		}
		if (posZ < minZ) {
			posZ = minZ;
		}
		else if (posZ > maxZ) {
			posZ = maxZ;
		}
		
		return new BlockPos(posX, posY, posZ);
	}
	
	public static boolean shouldDoGrave() {
		return true;//TODO: gamerule
	}
	
	public static int maxPortalWidth() {
		return 21;
	}
	
	public static int maxPortalHeight() {
		return 21;
	}
	
}
