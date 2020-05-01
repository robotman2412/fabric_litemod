package com.robotman2412.litemod.block.itemduct;

import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class GoldenItemductBlock extends AbstractItemductBlock {
	
	public GoldenItemductBlock() {
		super(Settings.of(Material.METAL), "golden_itemduct", true);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new GoldenItemductBlockEntity();
	}
	
}
