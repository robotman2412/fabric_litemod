package com.robotman2412.litemod.foods;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.BlockWrapper;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BLENDOMATOR9000 extends BlockWrapper implements BlockEntityProvider {
	
	public static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(5, 0, 5, 11, 12, 11);
	
	public final String colorName;
	public final String colorCode;
	
	public BLENDOMATOR9000(String colorName, String colorCode) {
		super(Settings.of(Material.STONE, MaterialColor.AIR).strength(0.5f, 1f), "blendomator_9000_" + colorName, true);
		this.colorCode = colorCode;
		this.colorName = colorName;
	}
	
	@Override
	public Text getName() {
		return new LiteralText(colorCode).append("\\u00A7l").append(new TranslatableText("block.robot_litemod.blendomator_9000"));
	}
	
	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
				world.updateNeighbors(pos, this);
			}
			super.onBlockRemoved(state, world, pos, newState, moved);
		}
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			BLENDOMATOR9000BlockEntity ent = (BLENDOMATOR9000BlockEntity) world.getBlockEntity(pos);
			ContainerProviderRegistry.INSTANCE.openContainer(FabricLitemod.BLENDOMATOR9000_CONTAINER, player, buf -> buf.writeBlockPos(pos));
		}
		return ActionResult.SUCCESS;
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
		return new BLENDOMATOR9000BlockEntity();
	}
	
}
