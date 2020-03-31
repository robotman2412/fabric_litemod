package com.robotman2412.litemod.block;

import com.robotman2412.litemod.PowerResult;
import com.robotman2412.litemod.RelativeDirection;
import net.minecraft.block.*;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.property.AbstractProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public abstract class AbstractRedstoneTileBlock extends BlockWrapper {
	
	public static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
	
	public final IOConfiguration ioConfiguration;
	
	public AbstractRedstoneTileBlock(Settings settings, String blockName, IOConfiguration ioConfiguration, boolean doBlockItem) {
		super(settings, blockName, doBlockItem);
		this.ioConfiguration = ioConfiguration;
	}
	
	@Override
	public Item.Settings getItemSettings() {
		return new Item.Settings().group(ItemGroup.REDSTONE);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
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
	public Material getMaterial(BlockState state) {
		return Material.PART;
	}
	
	public int getPowerForSide(RelativeDirection direction, World world, BlockState state, BlockPos pos) {
		IOMode ioMode = getIOModeForSide(direction);
		if (ioMode.mode != IOModeEnum.IN) {
			System.err.println("THIS IS A BUG: Direction power input is requested, but direction is not an input!" +
					"\nFor side: " + direction + ", of block: " + getTranslationKey() + ", which is facing: " + direction);
			return 0;
		}
		Direction sideDirection = direction.translate(state.get(FACING));
		BlockPos sideBlock = pos.add(sideDirection.getOffsetX(), 0, sideDirection.getOffsetZ());
		BlockState sideBlockState = world.getBlockState(sideBlock);
		PowerResult res = null;
		if (sideBlockState.isSimpleFullBlock(world, sideBlock)) {
			res = PowerResult.getInducedPower(world, sideBlock);
		}
		/*System.out.println("Relative side: " + direction + ", Side to source: " + sideDirection + ", Side from source: " + sideInwardsDirection + ", Side block pos: " + sideBlock);
		System.out.println(
				"Weak power for side block: " + sideBlockState.getWeakRedstonePower(world, sideBlock, sideInwardsDirection) +
				"\nStrong power for side block: " + sideBlockState.getStrongRedstonePower(world, sideBlock, sideInwardsDirection) +
				"\nWeak other power for side block: " + sideBlockState.getWeakRedstonePower(world, sideBlock, sideDirection) +
				"\nStrong other power for side block: " + sideBlockState.getStrongRedstonePower(world, sideBlock, sideDirection)
		);*/
		switch (ioMode.modeStrength) {
			default:
				throw new NullPointerException("THIS IS A BUG: IO mode strength is null!" +
						"\nFor side: " + direction + ", of block: " + getTranslationKey() + ", which is facing: " + direction);
			case STRONG:
				if (res != null) {
					return res.powerLevel;
				}
				else if (sideBlockState.getBlock() instanceof RedstoneWireBlock) {
					return sideBlockState.get(RedstoneWireBlock.POWER);
				}
				else {
					return Math.max(sideBlockState.getWeakRedstonePower(world, sideBlock, sideDirection), sideBlockState.getStrongRedstonePower(world, sideBlock, sideDirection));
				}
				//break;
			case WEAK:
				if (res != null) {
					return res.powerLevel & (res.isStronglyPowered ? 15 : 0);
				}
				else if (sideBlockState.getBlock() instanceof RedstoneWireBlock) {
					return sideBlockState.get(RedstoneWireBlock.POWER);
				}
				else {
					return Math.max(sideBlockState.getWeakRedstonePower(world, sideBlock, sideDirection), sideBlockState.getStrongRedstonePower(world, sideBlock, sideDirection));
				}
				//break;
			case SUPER_WEAK:
				return sideBlockState.getStrongRedstonePower(world, sideBlock, sideDirection);
				//break;
		}
	}
	
	public boolean isStrongPowerForSide(RelativeDirection direction, World world, BlockState state, BlockPos pos) {
		IOMode ioMode = getIOModeForSide(direction);
		if (ioMode.mode != IOModeEnum.IN) {
			System.err.println("THIS IS A BUG: Direction power input is requested, but direction is not an input!" +
					"\nFor side: " + direction + ", of block: " + getTranslationKey() + ", which is facing: " + direction);
			return false;
		}
		Direction sideDirection = direction.translate(state.get(FACING));
		Direction sideInwardsDirection = sideDirection.getOpposite();
		BlockPos sideBlock = pos.add(sideDirection.getOffsetX(), 0, sideDirection.getOffsetZ());
		BlockState sideBlockState = world.getBlockState(sideBlock);
		if (sideBlockState.isSimpleFullBlock(world, sideBlock)) {
			return PowerResult.getInducedPower(world, sideBlock).isStronglyPowered;
		}
		if (sideBlockState.getBlock() instanceof RedstoneWireBlock) {
			return false;
		}
		return sideBlockState.getStrongRedstonePower(world, sideBlock, sideDirection) > 0;
	}
	
	public IOMode getIOModeForSide(RelativeDirection direction) {
		switch (direction) {
			default:
				throw new NullPointerException("Direction is null!");
			case FORWARD:
				return ioConfiguration.forward;
			case BACKWARD:
				return ioConfiguration.backward;
			case LEFT:
				return ioConfiguration.left;
			case RIGHT:
				return ioConfiguration.right;
		}
	}
	
	public boolean getBooleanPowerForSide(RelativeDirection direction, World world, BlockState state, BlockPos pos) {
		return getPowerForSide(direction, world, state, pos) > 0;
	}
	
	public static RedstoneTileOutputProperty outputPropertyFor(String name, IOMode ioMode) {
		if (ioMode.mode == IOModeEnum.OUT) {
			return new RedstoneTileOutputProperty(name);
		}
		else
		{
			return null;
		}
	}
	
	public static class IOConfiguration {
		
		public final IOMode forward;
		public final IOMode backward;
		public final IOMode left;
		public final IOMode right;
		
		public IOConfiguration(IOMode forward, IOMode backward, IOMode left, IOMode right) {
			this.forward = forward;
			this.backward = backward;
			this.left = left;
			this.right = right;
		}
		
	}
	
	public static class IOMode {
		
		public final IOModeEnum mode;
		/**
		 * Means that the output power is seen as only on or off.
		 */
		public final boolean isBinary;
		public final IOModeStrengthEnum modeStrength;
		
		public IOMode(IOModeEnum mode, boolean isBinary, IOModeStrengthEnum modeStrength) {
			this.mode = mode;
			this.isBinary = isBinary;
			this.modeStrength = modeStrength;
		}
		
		public IOMode(IOModeEnum mode, boolean isBinary) {
			this.mode = mode;
			this.isBinary = isBinary;
			this.modeStrength = IOModeStrengthEnum.STRONG;
		}
		
		public IOMode(IOModeEnum mode, IOModeStrengthEnum modeStrength) {
			this.mode = mode;
			this.isBinary = false;
			this.modeStrength = modeStrength;
		}
		
		public IOMode(IOModeEnum mode) {
			this.mode = mode;
			this.isBinary = false;
			this.modeStrength = IOModeStrengthEnum.STRONG;
		}
		
		public IOMode () {
			this.mode = IOModeEnum.NONE;
			this.isBinary = false;
			this.modeStrength = IOModeStrengthEnum.SUPER_WEAK;
		}
		
	}
	
	public enum IOModeStrengthEnum {
		/**
		 * Recieves all power, including block inferred power.
		 * Sends strong power, powers weak inputs throug block inferred power.
		 */
		STRONG,
		/**
		 * Recieves only direct power, e.g. from redstone wire or strong block inferred power.
		 * Sends only weak power, only strongly powered I/O e.g. comparators / repeaters will pick this up through block inferred power.
		 */
		WEAK,
		/**
		 * Recieves power only directly from strong power inputs, not even redstone wire.
		 * Sends power the same as weak would.
		 */
		SUPER_WEAK
	}
	
	public enum IOModeEnum {
		NONE,
		IN,
		OUT
	}
	
	public static class Output implements Comparable<Output> {
		
		final int power;
		final boolean isWeak;
		
		public Output(int power, boolean isWeak) {
			this.power = power;
			this.isWeak = isWeak;
		}
		
		@Override
		public int compareTo(Output other) {
			if (other.isWeak && !isWeak) {
				return -1;
			} else if (!other.isWeak && isWeak) {
				return -1;
			} else {
				return other.power == power ? 0 : 1;
			}
		}
		
	}
	
	public static class RedstoneTileOutputProperty extends AbstractProperty<Output> {
		
		private static final Output[] allPossibleOutputs;
		
		static {
			allPossibleOutputs = new Output[32];
			for (int i = 0; i < 16; i ++) {
				allPossibleOutputs[i] = new Output(i, false);
				allPossibleOutputs[i + 16] = new Output(i, true);
			}
		}
		
		public RedstoneTileOutputProperty(String name) {
			super(name, Output.class);
		}
		
		@Override
		public Collection<Output> getValues() {
			return Arrays.asList(allPossibleOutputs);
		}
		
		@Override
		public Optional<Output> parse(String name) {
			boolean isWeak = name.charAt(0) == 'w';
			return Optional.of(new Output(Integer.parseInt(name.substring(1)), isWeak));
		}
		
		@Override
		public String name(Output value) {
			return (value.isWeak ? "w" : "s") + value.power;
		}
		
	}
	
}
