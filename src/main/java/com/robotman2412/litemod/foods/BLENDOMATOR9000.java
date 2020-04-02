package com.robotman2412.litemod.foods;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.BlockWrapper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BLENDOMATOR9000 extends BlockWrapper implements BlockEntityProvider {
	
	public static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(5, 0, 5, 11, 12, 11);
	
	public final String colorName;
	
	public BLENDOMATOR9000(String colorName) {
		super(Settings.of(Material.STONE, MaterialColor.AIR), "blendomator_9000_" + colorName, true);
		this.colorName = colorName;
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return DEFAULT_SHAPE;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return DEFAULT_SHAPE;
	}
	
	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.getTranslucent();
	}
	
	@Override
	public Item.Settings getItemSettings() {
		return new Item.Settings().group(FabricLitemod.KITCHEN_SUPPLIES);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
	}
	
	@Override
	public boolean hasBlockEntity() {
		return true;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return null;
	}
	
}
