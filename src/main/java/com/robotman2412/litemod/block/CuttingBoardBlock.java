package com.robotman2412.litemod.block;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.block.*;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CuttingBoardBlock extends BlockWrapper {
	
	public static final VoxelShape SHAPE = Block.createCuboidShape(2D, 0D, 2D, 14.0D, 1.0D, 14.0D);
	
	public static final EnumProperty<DamageLevel> DAMAGE_LEVEL = EnumProperty.of("damage_level", DamageLevel.class);
	
	public CuttingBoardBlock() {
		super(Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F), "cutting_board", true);
		setDefaultState(getDefaultState()
				.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
				.with(DAMAGE_LEVEL, DamageLevel.BRAND_NEW)
		);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
	}
	
	@Override
	public BlockSoundGroup getSoundGroup(BlockState state) {
		return BlockSoundGroup.WOOD;
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
		builder.add(DAMAGE_LEVEL);
	}
	
	@Override
	public Item.Settings getItemSettings() {
		return new Item.Settings().group(FabricLitemod.KITCHEN_SUPPLIES).maxCount(1).maxDamage(4096);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return SHAPE;
	}
	
	public enum DamageLevel implements StringIdentifiable {
		
		BRAND_NEW,
		SLIGHTLY_USED,
		USED,
		HEAVILY_USED;
		
		@Override
		public String asString() {
			return name().toLowerCase();
		}
		
	}
	
}
