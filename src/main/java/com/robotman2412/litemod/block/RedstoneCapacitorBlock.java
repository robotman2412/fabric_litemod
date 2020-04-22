package com.robotman2412.litemod.block;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.util.RelativeDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class RedstoneCapacitorBlock extends AbstractRedstoneTileBlock implements BlockEntityProvider {
	
	public static final BooleanProperty HOLD = BooleanProperty.of("hold");
	public static final EnumProperty<PowerEnum> POWER = EnumProperty.of("power", PowerEnum.class);
	
	public RedstoneCapacitorBlock() {
		super(Settings.copy(Blocks.REPEATER), "redstone_capacitor", new IOConfiguration(
				new IOMode(IOModeEnum.IN, false),
				new IOMode(IOModeEnum.IN, false),
				new IOMode(IOModeEnum.IN, true),
				new IOMode(IOModeEnum.IN, true)
		), true);
		setDefaultState(getStateManager().getDefaultState()
				.with(POWER, PowerEnum.OFF)
				.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
				.with(HOLD, false)
		);
		
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(
				POWER,
				Properties.HORIZONTAL_FACING,
				HOLD
		);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}
	
	@Override
	public boolean hasBlockEntity() {
		return true;
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		Direction facing = state.get(Properties.HORIZONTAL_FACING);
		BlockPos frontPos = pos.add(facing.getOffsetX(), 0, facing.getOffsetZ());
		BlockPos backPos = pos.add(-facing.getOffsetX(), 0, -facing.getOffsetZ());
		BlockState front = world.getBlockState(frontPos);
		BlockState back = world.getBlockState(backPos);
		if (front.isSimpleFullBlock(world, frontPos)) {
			world.updateNeighbors(frontPos, front.getBlock());
		}
		if (back.isSimpleFullBlock(world, backPos)) {
			world.updateNeighbors(backPos, back.getBlock());
		}
		RedstoneCapacitorBlockEntity ent = (RedstoneCapacitorBlockEntity) world.getBlockEntity(pos);
		ent.powerLevelIn = 0;
		ent.powerLevel = 0;
		world.updateNeighbors(pos, FabricLitemod.REDSTONE_CAPACITOR_BLOCK);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		neighborUpdate(state, world, pos, null, null, false);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		int powerIn0 = getPowerForSide(RelativeDirection.BACKWARD, world, state, pos);
		boolean powerInStrong0 = isStrongPowerForSide(RelativeDirection.BACKWARD, world, state, pos);
		int powerIn1 = getPowerForSide(RelativeDirection.BACKWARD, world, state, pos);
		boolean powerInStrong1 = isStrongPowerForSide(RelativeDirection.BACKWARD, world, state, pos);
		int powerIn;
		boolean powerInStrong;
		if (powerIn0 == powerIn1) {
			powerInStrong = powerInStrong0 || powerInStrong1;
			powerIn = powerIn0;
		}
		else if (powerIn0 < powerIn1) {
			powerInStrong = powerInStrong1;
			powerIn = powerIn1;
		}
		else {
			powerInStrong = powerInStrong0;
			powerIn = powerIn0;
		}
		boolean doLock = getBooleanPowerForSide(RelativeDirection.LEFT, world, state, pos) || getBooleanPowerForSide(RelativeDirection.RIGHT, world, state, pos);
		if (!powerInStrong) {
			powerIn --;
			if (powerIn < 0) {
				powerIn = 0;
			}
		}
		RedstoneCapacitorBlockEntity ent = (RedstoneCapacitorBlockEntity) world.getBlockEntity(pos);
		ent.powerLevelIn = powerIn;
		if (powerIn > ent.powerLevel) {
			ent.decrementTimer = 0;
		}
		ent.isLocked = doLock;
		world.setBlockState(pos, state.with(HOLD, doLock));
	}
	
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction facing = state.get(Properties.HORIZONTAL_FACING);
		RedstoneCapacitorBlockEntity ent = (RedstoneCapacitorBlockEntity) world.getBlockEntity(pos);
		if (ent == null) {
			return;
		}
		int powah;
		switch(state.get(POWER)) {
			default:
				powah = 0;
				break;
			case HALF:
				powah = 7;
				break;
			case ON:
				powah = 15;
				break;
		}
		stupidParticla(powah, state, world, pos, random, facing.getOffsetX() * 0.2, 0.1, facing.getOffsetZ() * 0.2);
		stupidParticla(powah, state, world, pos, random, facing.getOffsetX() * -0.2, 0.1, facing.getOffsetZ() * -0.2);
	}
	
	protected void stupidParticla(int i, BlockState state, World world, BlockPos pos, Random random, double xOffs, double yOffs, double zOffs) {
		if (i == 0) {
			return;
		}
		double x = pos.getX() + 0.5D + (random.nextFloat() - 0.5D) * 0.2D + xOffs;
		double y = (pos.getY() + 0.0625D) + yOffs;
		double z = pos.getZ() + 0.5D + (random.nextFloat() - 0.5D) * 0.2D + zOffs;
		float m = i / 15.0F;
		float r = m * 0.6F + 0.4F;
		float g = Math.max(0.0F, m * m * 0.7F - 0.5F);
		float b = Math.max(0.0F, m * m * 0.6F - 0.7F);
		world.addParticle(new DustParticleEffect(r, g, b, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		RelativeDirection side = RelativeDirection.inverseTranslate(state.get(Properties.HORIZONTAL_FACING), facing);
		if (side == RelativeDirection.FORWARD || side == RelativeDirection.BACKWARD) {
			RedstoneCapacitorBlockEntity ent = (RedstoneCapacitorBlockEntity) view.getBlockEntity(pos);
			return ent.powerLevel;
		}
		return 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return getWeakRedstonePower(state, view, pos, facing);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new RedstoneCapacitorBlockEntity();
	}
	
	public enum PowerEnum implements StringIdentifiable {
		OFF,
		HALF,
		ON;
		
		public static PowerEnum from(int powerLevel) {
			if (powerLevel > 11) {
				return ON;
			}
			if (powerLevel > 4) {
				return HALF;
			}
			return OFF;
		}
		
		@Override
		public String asString() {
			return name().toLowerCase();
		}
	}
	
}
