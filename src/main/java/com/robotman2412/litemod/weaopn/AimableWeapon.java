package com.robotman2412.litemod.weaopn;

import com.robotman2412.litemod.item.ItemWrapper;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class AimableWeapon extends ItemWrapper {
	
	public AimableWeapon(Settings settings, String itemName) {
		super(settings, itemName);
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		tryFire(world, user, user.getActiveHand(), stack);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		//tryFire(world, user, hand, user.getStackInHand(hand));
		user.setCurrentHand(hand);
		return TypedActionResult.consume(user.getStackInHand(hand));
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		//tryFire(context.getWorld(), context.getPlayer(), context.getHand(), context.getStack());
		return ActionResult.PASS;
	}
	
	@Override
	public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		tryFire(user.getEntityWorld(), user, hand, stack);
		return false;
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}
	
	public void tryFire(World world, LivingEntity shooter, Hand hand, ItemStack stack) {
		double maxDist = getMaximumDistance(stack);
		//raytrace blocks
		Vec3d firePos = shooter.getCameraPosVec(1);
		HitResult res = rayTrace(shooter, maxDist, 1, false);
		HitResult.Type type = res.getType();
		//raytrace entities
		Box box = new Box(-maxDist, -maxDist, -maxDist, maxDist, maxDist, maxDist).offset(firePos);
		Vec3d vec3d2 = shooter.getRotationVec(1.0F);
		Vec3d traceDir = firePos.add(vec3d2.x * maxDist, vec3d2.y * maxDist, vec3d2.z * maxDist);
		EntityHitResult entityHitResult = ProjectileUtil.rayTrace(shooter, firePos, traceDir, box, (entityx) -> {
			return !entityx.isSpectator() && entityx.collides();
		}, maxDist * maxDist);
		//raytrace fluids
		HitResult fluidable = rayTrace(shooter, maxDist, 1, true);
		FluidState firstFluidHit = null;
		Vec3d firstFluitHitPos = null;
		if (fluidable instanceof BlockHitResult) {
			firstFluidHit = world.getBlockState(((BlockHitResult) fluidable).getBlockPos()).getFluidState();
			firstFluitHitPos = fluidable.getPos();
		}
		//misc hit checks
		Vec3d pos = null;
		double dist = Double.POSITIVE_INFINITY;
		if (type != HitResult.Type.MISS) {
			pos = res.getPos();
			dist = firePos.squaredDistanceTo(pos);
		}
		double entityDist;
		if (entityHitResult != null) {
			entityDist = firePos.squaredDistanceTo(entityHitResult.getPos());
			if (entityDist < dist) {
				res = entityHitResult;
				pos = res.getPos();
				dist = firePos.squaredDistanceTo(pos);
			}
		}
		FireingContext context = new FireingContext(
				shooter.headYaw,
				shooter.pitch,
				firePos,
				pos,
				dist,
				stack,
				firstFluidHit,
				firstFluitHitPos,
				shooter.getRotationVec(1)
		);
		if (!preFire(world, shooter, context)) {
			return; //preFire can cancel if needed
		}
		if (res instanceof BlockHitResult) {
			BlockHitResult blockRes = (BlockHitResult) res;
			BlockPos hitBlock = blockRes.getBlockPos();
			BlockState hitBlockState = world.getBlockState(hitBlock);
			if (hitBlockState.isAir() || context.hit == null) {
				onNoHit(world, shooter, context);
			}
			else
			{
				onBlockHit(world, shooter, context, hitBlock, hitBlockState);
			}
		}
		else if (res instanceof EntityHitResult) {
			EntityHitResult entityRes = (EntityHitResult) res;
			Entity hitEntity = entityRes.getEntity();
			onEntityHit(world, shooter, context, hitEntity);
		}
		else
		{
			onNoHit(world, shooter, context);
		}
	}
	
	public static HitResult rayTrace(LivingEntity player, double maxDistance, int tickDelta, boolean includeFluids) {
		double offset = 0;
		BlockHitResult res = null;
		Vec3d cameraPos = player.getCameraPosVec(tickDelta);
		while (true) {
			double distanceToTravel = maxDistance - offset;
			if (distanceToTravel < 0.2 && res != null) {
				return res;
			}
			res = rayTraceBit(player, distanceToTravel, tickDelta, includeFluids, offset);
			BlockPos checkedPos = res.getBlockPos();
			if (!shouldIgnoreHitFor(player.world, checkedPos, player.world.getBlockState(checkedPos), includeFluids)) {
				return res;
			}
			offset = res.getPos().distanceTo(cameraPos) + 0.5;
		}
	}
	
	public static boolean shouldIgnoreHitFor(BlockView world, BlockPos pos, BlockState state, boolean includeFluids) {
		Block block = state.getBlock();
		if (includeFluids && block instanceof FluidBlock) {
			return false;
		}
		return block instanceof PlantBlock || block == Blocks.IRON_BARS || block instanceof CarpetBlock
				|| block instanceof LeavesBlock || block instanceof SugarCaneBlock || state.getCollisionShape(world, pos).isEmpty();
	}
	
	private static BlockHitResult rayTraceBit(LivingEntity player, double maxDistance, int tickDelta, boolean includeFluids, double offset) {
		Vec3d cameraPos = player.getCameraPosVec(tickDelta);
		Vec3d rotation = player.getRotationVec(tickDelta);
		Vec3d offsetVec = rotation.multiply(offset);
		cameraPos = cameraPos.add(offsetVec);
		Vec3d direction = cameraPos.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
		return player.world.rayTrace(new RayTraceContext(cameraPos, direction, RayTraceContext.ShapeType.OUTLINE, includeFluids ? RayTraceContext.FluidHandling.ANY : RayTraceContext.FluidHandling.NONE, player));
	}
	
	protected abstract double getMaximumDistance(ItemStack stack);
	
	public abstract boolean preFire(World world, LivingEntity shooter, FireingContext context);
	
	public abstract void onBlockHit(World world, LivingEntity shooter, FireingContext context, BlockPos pos, BlockState state);
	
	public abstract void onEntityHit(World world, LivingEntity shooter, FireingContext context, Entity hitEntity);
	
	public abstract void onNoHit(World world, LivingEntity shooter, FireingContext context);
	
	public static class FireingContext {
		
		public final double yaw;
		public final double pitch;
		public final Vec3d firedFrom;
		public final Vec3d hit;
		public final double squaredDistanceTraveled;
		public final ItemStack stack;
		public final FluidState firstFluidHit;
		public final Vec3d firstFluitHitPos;
		public final Vec3d direction;
		
		public FireingContext(double yaw, double pitch, Vec3d firedFrom, Vec3d hit, double squaredDistanceTraveled, ItemStack stack, FluidState firstFluidHit, Vec3d firstFluitHitPos, Vec3d direction) {
			this.yaw = yaw;
			this.pitch = pitch;
			this.firedFrom = firedFrom;
			this.hit = hit;
			this.squaredDistanceTraveled = squaredDistanceTraveled;
			this.stack = stack;
			this.firstFluidHit = firstFluidHit;
			this.firstFluitHitPos = firstFluitHitPos;
			this.direction = direction;
		}
		
	}
	
}
