package com.robotman2412.litemod.block.grave;

import com.mojang.authlib.GameProfile;
import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.BlockWrapper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class TheStoneOfGravesBlock extends BlockWrapper implements BlockEntityProvider {
	
	static {
		VoxelShape shape0 = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
		VoxelShape shape1North = Block.createCuboidShape(3, 1, 1, 13, 12, 2);
		VoxelShape shape2North = Block.createCuboidShape(4, 12, 1, 12, 13, 2);
		SHAPE_NORTH = VoxelShapes.union(shape0, shape1North, shape2North);
		VoxelShape shape1East = Block.createCuboidShape(15, 1, 3, 14, 12, 13);
		VoxelShape shape2East = Block.createCuboidShape(15, 12, 4, 14, 13, 12);
		SHAPE_EAST = VoxelShapes.union(shape0, shape1East, shape2East);
		VoxelShape shape1South = Block.createCuboidShape(3, 1, 15, 13, 12, 14);
		VoxelShape shape2South = Block.createCuboidShape(4, 12, 15, 12, 13, 14);
		SHAPE_SOUTH = VoxelShapes.union(shape0, shape1South, shape2South);
		VoxelShape shape1West = Block.createCuboidShape(1, 1, 3, 2, 12, 13);
		VoxelShape shape2West = Block.createCuboidShape(1, 12, 4, 2, 13, 12);
		SHAPE_WEST = VoxelShapes.union(shape0, shape1West, shape2West);
	}
	
	public static final VoxelShape SHAPE_NORTH;
	public static final VoxelShape SHAPE_EAST;
	public static final VoxelShape SHAPE_SOUTH;
	public static final VoxelShape SHAPE_WEST;
	
	public static final EnumProperty<GraveType> GRAVE_TYPE = EnumProperty.of("grave_type", GraveType.class);
	
	public static int graveSearchHorizontal = 5;
	public static int graveSearchVertical = 10;
	public TheStoneOfGravesBlock() {
		super(Settings.of(Material.STONE).strength(2.5f, 100000f), "the_stone_of_graves", true);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
		builder.add(GRAVE_TYPE);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		switch (state.get(Properties.HORIZONTAL_FACING)) {
			case NORTH:
				return SHAPE_NORTH;
			case EAST:
				return SHAPE_EAST;
			case SOUTH:
				return SHAPE_SOUTH;
			case WEST:
				return SHAPE_WEST;
		}
		return SHAPE_NORTH;
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		TheStoneOfGravesBlockEntity tileEntity = (TheStoneOfGravesBlockEntity) world.getBlockEntity(pos);
		assert tileEntity != null;
		if (player.getUuid().equals(tileEntity.owner)) {
			//do the gibv
			tileEntity.gibvAllYouCan(player.inventory);
			//TODO: open GUI if not empty or something
			return ActionResult.CONSUME;
		}
		return ActionResult.PASS;
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		TheStoneOfGravesBlockEntity tileEntity = (TheStoneOfGravesBlockEntity) world.getBlockEntity(pos);
		assert tileEntity != null;
		if (player.getUuid().equals(tileEntity.owner)) {
			//first do the gibv
			tileEntity.gibvAllYouCan(player.inventory);
			//scatter the rest i guess
			ItemScatterer.spawn(world, pos, tileEntity);
			tileEntity.clear();
		}
		super.onBreak(world, pos, state, player);
	}
	
	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			TheStoneOfGravesBlockEntity ent = (TheStoneOfGravesBlockEntity) world.getBlockEntity(pos);
			ItemScatterer.spawn(world, pos, ent);
			world.removeBlockEntity(pos);
		}
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.getOrCreateTag().contains("BlockEntityTag")) {
			boolean isThePeti = !itemStack.getOrCreateSubTag("BlockEntityTag").getString("lastKnownOwnerName").isEmpty();
			world.setBlockState(pos, world.getBlockState(pos).with(GRAVE_TYPE, isThePeti ? GraveType.DOG : GraveType.PLAYER));
		}
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new TheStoneOfGravesBlockEntity();
	}
	
	@Override
	public boolean hasBlockEntity() {
		return true;
	}
	
	@Override
	public void buildTooltip(ItemStack stack, BlockView view, List<Text> tooltip, TooltipContext options) {
		if (stack.getOrCreateTag().contains("BlockEntityTag")) {
			String staxn = stack.getOrCreateTag().getCompound("BlockEntityTag").getString("lastKnownName");
			tooltip.add(new LiteralText("R.I.P. " + staxn));
		}
	}
	
	public static boolean createSuitableGrave(LivingEntity entity, Inventory entityInventory, GraveType graveType) {
		if (entity.world.isClient) {
			return true;
		}
		
		int xOffset = (int) entity.getX();
		int yOffset = (int) entity.getY();
		int zOffset = (int) entity.getZ();
		
		//first try this
		double closestNonDestructiveSqrDist = Double.POSITIVE_INFINITY;
		BlockPos closestSuitableNonDestructive = null;
		//as a fallback, sacrafice cheap blocks
		double closestSacraficialSqrDist = Double.POSITIVE_INFINITY;
		BlockPos closestSuitableSacraficialPos = null;
		
		World world = entity.world;
		
		//search for a spot
		for (int y = 0; y >= -graveSearchVertical; y--) {
			for (int x = -graveSearchHorizontal; x <= graveSearchHorizontal; x++) {
				for (int z = -graveSearchHorizontal; z <= graveSearchHorizontal; z++) {
					BlockPos posIn = new BlockPos(x + xOffset, y + yOffset, z + zOffset);
					BlockState state = world.getBlockState(posIn);
					double sqrDist = posIn.getSquaredDistance(entity.getBlockPos());
					if (isFreeSpace(state) && sqrDist < closestNonDestructiveSqrDist) {
						closestNonDestructiveSqrDist = sqrDist;
						closestSuitableNonDestructive = posIn;
					}
					else if (isSacraficialSpace(state) && sqrDist < closestSacraficialSqrDist) {
						closestSacraficialSqrDist = sqrDist;
						closestSuitableSacraficialPos = posIn;
					}
				}
			}
		}
		
		for (int y = 0; y <= graveSearchVertical; y++) {
			for (int x = -graveSearchHorizontal; x <= graveSearchHorizontal; x++) {
				for (int z = -graveSearchHorizontal; z <= graveSearchHorizontal; z++) {
					BlockPos posIn = new BlockPos(x + xOffset, y + yOffset, z + zOffset);
					if (posIn.getY() < 0 || posIn.getY() > 255 || !world.isChunkLoaded(posIn)) {
						continue;
					}
					BlockState state = world.getBlockState(posIn);
					double sqrDist = posIn.getSquaredDistance(entity.getBlockPos());
					if (isFreeSpace(state) && sqrDist < closestNonDestructiveSqrDist) {
						closestNonDestructiveSqrDist = sqrDist;
						closestSuitableNonDestructive = posIn;
					}
					else if (isSacraficialSpace(state) && sqrDist < closestSacraficialSqrDist) {
						closestSacraficialSqrDist = sqrDist;
						closestSuitableSacraficialPos = posIn;
					}
				}
			}
		}
		
		if (closestSuitableNonDestructive != null) {
			return putGrave(world, closestSuitableNonDestructive, entity, entityInventory, graveType);
		}
		else if (closestSuitableSacraficialPos != null) {
			return putGrave(world, closestSuitableSacraficialPos, entity, entityInventory, graveType);
		}
		
		return false;
	}
	
	public static boolean mayBeReplacedByGrave(World world, BlockPos pos) {
		return true;
	}
	
	public static boolean putGrave(World world, BlockPos pos, LivingEntity entity, Inventory entityInventory, GraveType graveType) {
		if (!mayBeReplacedByGrave(world, pos)) {
			return false;
		}
		
		Direction facing = entity.getHorizontalFacing();
		
		BlockState replaced = world.getBlockState(pos);
		BlockState newState = FabricLitemod.THE_STONE_OF_GRAVES_BLOCK.getDefaultState()
				.with(GRAVE_TYPE, graveType).with(Properties.HORIZONTAL_FACING, facing);
		//clean up the stuffs
		replaced.onBlockRemoved(world, pos, newState, false);
		//put out block
		world.setBlockState(pos, newState);
		TheStoneOfGravesBlockEntity tileEntity = (TheStoneOfGravesBlockEntity) world.getBlockEntity(pos);
		
		if (entity instanceof TameableEntity) {
			tileEntity.owner = ((TameableEntity) entity).getOwnerUuid();
			GameProfile profile = world.getServer().getUserCache().getByUuid(tileEntity.owner);
			if (profile != null) {
				tileEntity.lastKnownOwnerName = profile.getName();
			}
		}
		else
		{
			tileEntity.owner = entity.getUuid();
		}
		tileEntity.lastKnownName = entity.getDisplayName().asString();
		if (entityInventory != null) {
			tileEntity.slurpUpItems(entityInventory);
		}
		world.setBlockEntity(pos, tileEntity);
		
		//send an update afterwards
		world.updateNeighbors(pos, FabricLitemod.THE_STONE_OF_GRAVES_BLOCK);
		
		return true;
	}
	
	public static boolean isSacraficialSpace(BlockState state) {
		Block block = state.getBlock();
		return block == Blocks.DIRT || block == Blocks.GRASS || block instanceof PlantBlock || block instanceof FluidBlock;
	}
	
	public static boolean isFreeSpace(BlockState state) {	
		return state.getBlock() instanceof AirBlock;
	}
	
	public enum GraveType implements StringIdentifiable {
		
		PLAYER,
		DOG;
		
		@Override
		public String asString() {
			return name().toLowerCase();
		}
		
	}
	
}
