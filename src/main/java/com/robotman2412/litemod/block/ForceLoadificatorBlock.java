package com.robotman2412.litemod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ForceLoadificatorBlock extends BlockWrapper {
	
	public ForceLoadificatorBlock() {
		super(Settings.of(Material.STONE), "chunk_loader", true);
	}
	
	@Override
	public Item.Settings getItemSettings() {
		return new Item.Settings().group(ItemGroup.REDSTONE);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		int x = chunkX(pos);
		int z = chunkZ(pos);
		System.out.println("Chunk loader placed in chunk " + x + ", " + z);
	}
	
	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		int x = chunkX(pos);
		int z = chunkZ(pos);
		System.out.println("Chunk loader removed from chunk " + x + ", " + z);
	}
	
	public static int chunkX(BlockPos pos) {
		return (int) Math.floor(pos.getX() / 16f);
	}
	
	public static int chunkZ(BlockPos pos) {
		return (int) Math.floor(pos.getZ() / 16f);
	}
	
}
