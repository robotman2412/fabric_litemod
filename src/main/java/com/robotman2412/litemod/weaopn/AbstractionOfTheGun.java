package com.robotman2412.litemod.weaopn;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.mixinutil.CanCancelStuff;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.WeakHashMap;

public abstract class AbstractionOfTheGun extends AimableWeapon implements CanCancelStuff {
	
	public static boolean doTheBigCancel = false;
	public static WeakHashMap<ItemStack, LastFireBullshite> map = new WeakHashMap<ItemStack, LastFireBullshite>();
	
	@Override
	public boolean cancelEquipProgressReset(ItemStack stack) {
		boolean Le = doTheBigCancel;
		doTheBigCancel = false;
		return Le;
	}
	
	public AbstractionOfTheGun(String itemName) {
		super(new Settings().maxCount(1).group(FabricLitemod.WEAPONS), itemName);
	}
	
	public int getMinimumDamage(ItemStack stack) {
		return getMaximumDamage(stack);
	}
	
	public float getRandomDamage(ItemStack stack) {
		int min = getMinimumDamage(stack);
		int max = getMaximumDamage(stack);
		Random random = new Random();
		return min + (max - min) * random.nextFloat();
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		onStoppedUsing(stack, world, user, 0);
		return super.finishUsing(stack, world, user);
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		super.onStoppedUsing(stack, world, user, remainingUseTicks);
		stack.getOrCreateTag().remove("shots_fired");
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return Integer.MAX_VALUE;
	}
	
	public abstract int getFireDelayTicks(WeaponMode mode);
	
	public abstract WeaponMode[] getPossibleModes(ItemStack stack);
	
	public WeaponMode getMode(ItemStack stack) {
		return getPossibleModes(stack)[0];
	}
	
	public abstract int getMaximumDamage(ItemStack stack);
	
	@Override
	public boolean preFire(World world, LivingEntity shooter, FireingContext context) {
		ItemStack stack = shooter.getActiveItem();
		LastFireBullshite shite = map.get(stack);
		WeaponMode mode = getMode(stack);
		if (shite == null) {
			shite = new LastFireBullshite(world.getTime(), 1);
			map.put(stack, shite);
		}
		else if (shite.lastFireTick + getFireDelayTicks(mode) > world.getTime()) {
			return false;
		}
		else if (!mode.isAutomatic && mode.shotsFired <= shite.consecutiveShots) {
			return false;
		}
		shite.lastFireTick = world.getTime();
		CompoundTag tag = context.stack.getOrCreateTag();
		if (context.firstFluidHit != null) {
			if (context.firstFluidHit.getFluid() instanceof WaterFluid) {
				Vec3d hit = context.firstFluitHitPos;
				world.playSound(hit.x, hit.y, hit.z, new SoundEvent(new Identifier("minecraft", "entity.generic.splash")), SoundCategory.BLOCKS, 0.5f, 2f, true);
			}
		}
		//TODO: consume ammo
		doTheBigCancel = true;
		return true;
	}
	
	@Override
	public void onNoHit(World world, LivingEntity shooter, FireingContext context) {
		//do nothing
	}
	
	@Override
	public void onBlockHit(World world, LivingEntity shooter, FireingContext context, BlockPos pos, BlockState state) {
		if (world.isClient) {
			ParticleEffect effect = new DustParticleEffect(0.3f, 0.3f, 0.3f, world.random.nextFloat() * 0.2f + 0.6f);
			double x = context.hit.x;
			double y = context.hit.y;
			double z = context.hit.z;
			double mult = 0.065125;
			int num = world.random.nextInt(3) + 2;
			for (int i = 0; i < num; i++) {
				world.addParticle(
						effect,
						x + (world.random.nextDouble() * 2 - 1) * mult,
						y + (world.random.nextDouble() * 2 - 1) * mult + mult,
						z + (world.random.nextDouble() * 2 - 1) * mult,
						0,
						0.1,
						0
				);
			}
		}
		if (!world.isClient && (
				state.getBlock() instanceof StainedGlassBlock
				|| state.getBlock() instanceof GlassBlock
				|| state.getBlock() instanceof StainedGlassPaneBlock
				|| state.getBlock() == Blocks.GLASS_PANE
				)) {
			world.breakBlock(pos, true);
		}
	}
	
	@Override
	public void onEntityHit(World world, LivingEntity shooter, FireingContext context, Entity hitEntity) {
		if (!hitEntity.isAlive()) {
			return;
		}
		float damageDealt = getRandomDamage(context.stack);
		boolean headshot = false;
		if (hitEntity instanceof LivingEntity) {
			//try headshot
			double eyeDist = Math.abs(context.hit.y - hitEntity.getEyeY());
			double maxEyeDist = hitEntity.getDimensions(hitEntity.getPose()).height / 6d;
			if (eyeDist < maxEyeDist) {
				damageDealt *= 2;
				headshot = true;
			}
		}
		hitEntity.damage(new EntityDamageSource("gunshot", shooter), damageDealt);
		if (shooter instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) shooter).playSound(new SoundEvent(
					new Identifier("minecraft", "entity.experience_orb.pickup")
			), SoundCategory.MASTER, headshot ? 0.75f : 0.5f, headshot ? 1f : 0.25f);
		}
	}
	
	public static class LastFireBullshite {
		
		public long lastFireTick;
		public int consecutiveShots;
		
		public LastFireBullshite() {
			
		}
		
		public LastFireBullshite(long lastFireTick, int consecutiveShots) {
			this.lastFireTick = lastFireTick;
			this.consecutiveShots = consecutiveShots;
		}
		
	}
}
