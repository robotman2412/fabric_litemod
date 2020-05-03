package com.robotman2412.litemod.weaopn;

import com.robotman2412.litemod.ClientEntry;
import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.util.PiercingEntityDamageSource;
import com.robotman2412.litemod.weaopn.ammo.MunitionItem;
import com.robotman2412.litemod.weaopn.ammo.MunitionType;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

public abstract class AbstractionOfTheGun extends AimableWeapon {
	
	public static WeakHashMap<ItemStack, LastFireBullshite> map = new WeakHashMap<>();
	
	public AbstractionOfTheGun(Settings settings, String itemName) {
		super(settings.maxCount(1).group(FabricLitemod.WEAPONS), itemName);
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
		LastFireBullshite shite = map.get(stack);
		if (shite != null) {
			int ammo = Math.max(0, getRemainingAmmo(stack) - shite.consecutiveShots);
			stack.getOrCreateTag().putInt("ammo", ammo);
			shite.consecutiveShots = 0;
			shite.didSendAmmoWarning = false;
			shite.lastFireTick -= 10;
		}
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		int diam = getBulletDiameter(stack);
		int maxLen = getMaxBulletLength(stack);
		WeaponMode activeMode = getMode(stack);
		WeaponMode[] modes = getPossibleModes(stack);
		String sFireRate = "" + Math.round(20f / getFireDelayTicks(activeMode) * 100) / 100f;
		tooltip.add(new TranslatableText("robot_litemod.weapon.diameter").append("" + diam + "mm"));
		tooltip.add(new TranslatableText("robot_litemod.weapon.max_length").append("" + maxLen + "mm"));
		tooltip.add(new TranslatableText("robot_litemod.weapon.mode").append(new TranslatableText(activeMode.getTranslationKey())));
		tooltip.add(new TranslatableText("robot_litemod.weapon.rate_of_fire").append(sFireRate).append(new TranslatableText("robot_litemod.weapon.rate_of_fire.suffix")));
		tooltip.add(new TranslatableText("robot_litemod.weapon.remaining_ammo").append("" + getRemainingAmmo(stack)));
	}
	
	//region misc
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return Integer.MAX_VALUE;
	}
	
	public abstract int getFireDelayTicks(WeaponMode mode);
	
	public abstract WeaponMode[] getPossibleModes(ItemStack stack);
	
	public MunitionType getMunitionType(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains("ammo_type")) {
			return null;
		}
		String name = stack.getTag().getString("ammo_type");
		for (MunitionType type : MunitionType.registry) {
			if (type.name.equals(name)) {
				return type;
			}
		}
		return null;
	}
	
	public WeaponMode getMode(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains("mode")) {
			return getPossibleModes(stack)[0];
		}
		else
		{
			WeaponMode mode = WeaponMode.getMode(new Identifier(stack.getTag().getString("mode")));
			if (mode == null) {
				return getPossibleModes(stack)[0];
			}
			return mode;
		}
	}
	
	public static int getRemainingAmmo(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains("ammo")) {
			return 0;
		}
		else
		{
			return stack.getTag().getInt("ammo");
		}
	}
	
	public abstract int getMaximumDamage(ItemStack stack);
	
	public abstract float getMinRecoil(ItemStack stack);
	
	public abstract float getMaxRecoil(ItemStack stack);
	
	public abstract int getBulletDiameter(ItemStack stack);
	
	public abstract int getMaxBulletLength(ItemStack stack);
	
	public SoundEffectWrapper getFireSound(ItemStack stack) {
		return new SoundEffectWrapper(new Identifier("entity.generic.explode"), 0.5f, 3);
	}
	
	public SoundEffectWrapper getReloadSound(ItemStack stack) {
		return new SoundEffectWrapper(new Identifier("block.piston.contract"), 2, 2);
	}
	//endregion misc
	
	//region fire
	@Override
	public boolean preFire(World world, LivingEntity shooter, FireingContext context) {
		ItemStack stack = shooter.getActiveItem();
		LastFireBullshite shite = map.get(stack);
		WeaponMode mode = getMode(stack);
		if (shite == null) {
			shite = new LastFireBullshite(world.getTime() - getFireDelayTicks(mode), 0);
			map.put(stack, shite);
		}
		if (shite.lastFireTick + getFireDelayTicks(mode) > world.getTime()) {
			return false;
		}
		else if (!mode.isAutomatic && mode.shotsFired <= shite.consecutiveShots) {
			return false;
		}
		else if (shite.consecutiveShots >= getRemainingAmmo(stack)) {
			if (!shite.didSendAmmoWarning) {
				if (shooter instanceof ServerPlayerEntity) {
					((ServerPlayerEntity) shooter).sendChatMessage(new TranslatableText("robot_litemod.weapon.magazine_empty"), MessageType.GAME_INFO);
				}
				int ammo = Math.max(0, getRemainingAmmo(stack) - shite.consecutiveShots);
				stack.getOrCreateTag().putInt("ammo", ammo);
				shite.consecutiveShots = 0;
			}
			shite.didSendAmmoWarning = true;
			return false;
		}
		shite.lastFireTick = world.getTime();
		shite.consecutiveShots ++;
		if (shite.consecutiveShots >= getRemainingAmmo(stack)) {
			if (!shite.didSendAmmoWarning) {
				if (shooter instanceof ServerPlayerEntity) {
					((ServerPlayerEntity) shooter).sendChatMessage(new TranslatableText("robot_litemod.weapon.magazine_empty"), MessageType.GAME_INFO);
				}
				int ammo = Math.max(0, getRemainingAmmo(stack) - shite.consecutiveShots);
				stack.getOrCreateTag().putInt("ammo", ammo);
				shite.consecutiveShots = 0;
			}
			shite.didSendAmmoWarning = true;
		}
		if (context.firstFluidHit != null) {
			if (context.firstFluidHit.getFluid() instanceof WaterFluid) {
				Vec3d hit = context.firstFluitHitPos;
				world.playSound(hit.x, hit.y, hit.z, new SoundEvent(new Identifier("minecraft", "entity.generic.splash")), SoundCategory.PLAYERS, 0.75f, 1.5f, true);
			}
		}
		if (world.isClient) {
			ClientEntry.recoil(
					-world.random.nextFloat() * Math.abs(getMaxRecoil(stack) - getMinRecoil(stack)) - getMinRecoil(stack),
					(world.random.nextFloat() - 0.5f) * getMaxRecoil(stack)
			);
			SoundEffectWrapper wrapper = getFireSound(stack);
			world.playSound(context.firedFrom.x, context.firedFrom.y - 0.5, context.firedFrom.z, wrapper.sound, SoundCategory.PLAYERS, wrapper.volume, wrapper.pitch, true);
		}
		return true;
	}
	
	@Override
	public void onNoHit(World world, LivingEntity shooter, FireingContext context) {
		//do nothing
	}
	
	@Override
	public void onBlockHit(World world, LivingEntity shooter, FireingContext context, BlockPos pos, BlockState state) {
		if (world.isClient) {
			MaterialColor coloriser = state.getTopMaterialColor(world, pos);
			int red =   (coloriser.color & 0xff0000) >> 16;
			int green = (coloriser.color & 0x00ff00) >> 8;
			int blue =  (coloriser.color & 0x0000ff);
			ParticleEffect effect = new DustParticleEffect(red / 255f, green / 255f, blue / 255f, world.random.nextFloat() * 0.4f + 0.9f);
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
		Block block = state.getBlock();
		if (block instanceof TntBlock) {
			TntBlock.primeTnt(world, pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			world.updateNeighbors(pos, block);
		}
	}
	
	@Override
	public void onEntityHit(World world, LivingEntity shooter, FireingContext context, Entity hitEntity) {
		if (!hitEntity.isAlive()) {
			return;
		}
		MunitionType type = getMunitionType(context.stack);
		float damageDealt = getRandomDamage(context.stack);
		boolean headshot = false;
		if (hitEntity instanceof LivingEntity) {
			//try headshot
			double eyeDist = Math.abs(context.hit.y - hitEntity.getEyeY());
			double maxEyeDist = hitEntity.getDimensions(hitEntity.getPose()).height / 7d;
			if (eyeDist < maxEyeDist) {
				damageDealt *= 5;
				headshot = true;
			}
		}
		if (type != null) {
			damageDealt *= type.damageMultiplier;
			if (type.isArmorPiercing) {
				hitEntity.damage(new PiercingEntityDamageSource("gunshot", shooter), damageDealt);
			}
			else
			{
				hitEntity.damage(new EntityDamageSource("gunshot", shooter), damageDealt);
			}
		}
		else
		{
			hitEntity.damage(new EntityDamageSource("gunshot", shooter), damageDealt);
		}
		if (type != null && type.extraKnockback > 0) {
			Vec3d knockback = context.direction.multiply(type.extraKnockback);
			hitEntity.addVelocity(knockback.x, knockback.y * -0.25, knockback.z);
		}
		if (shooter instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) shooter).playSound(new SoundEvent(
					new Identifier("minecraft", "entity.experience_orb.pickup")
			), SoundCategory.MASTER, headshot ? 0.75f : 0.5f, headshot ? 1f : 0.0005f);
		}
	}
	//endregion fire
	
	@Environment(EnvType.CLIENT)
	public static void sendSwitchMode(Hand hand) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeBoolean(hand == Hand.MAIN_HAND);
		ClientSidePacketRegistry.INSTANCE.sendToServer(FabricLitemod.WEAPON_SWITCH_MODE_PACKET, buffer);
	}
	
	@Environment(EnvType.CLIENT)
	public static void sendReload(PlayerEntity player, Hand hand) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeBoolean(hand == Hand.MAIN_HAND);
		ClientSidePacketRegistry.INSTANCE.sendToServer(FabricLitemod.WEAPON_RELOAD_PACKET, buffer);
		if (!(player.getStackInHand(hand).getItem() instanceof AbstractionOfTheGun)) {
			return;
		}
		ItemStack gunneStack = player.getStackInHand(hand);
		AbstractionOfTheGun gunne = (AbstractionOfTheGun) gunneStack.getItem();
		if (getRemainingAmmo(gunneStack) > 0) {
			return;
		}
		CompoundTag tag = gunneStack.getOrCreateTag();
		for (int i = 0; i < player.inventory.getInvSize(); i++) {
			ItemStack stack = player.inventory.getInvStack(i);
			if (stack.getItem() instanceof MunitionItem) {
				MunitionType type = ((MunitionItem) stack.getItem()).type;
				int count = ((MunitionItem) stack.getItem()).getCount(stack);
				if (count == 0 || type.length > gunne.getMaxBulletLength(gunneStack) || type.diameter != gunne.getBulletDiameter(gunneStack)) {
					continue;
				}
				tag.putInt("ammo", count);
				tag.putString("ammo_type", type.name);
				break;
			}
		}
	}
	
	public static void switchModePacket(PacketContext context, PacketByteBuf buffer) {
		Hand hand = buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		ItemStack stack = context.getPlayer().getStackInHand(hand);
		if (!(stack.getItem() instanceof AbstractionOfTheGun)) {
			return;
		}
		AbstractionOfTheGun gun = (AbstractionOfTheGun) stack.getItem();
		WeaponMode mode = gun.getMode(stack);
		int indexial = 0;
		WeaponMode[] modes = gun.getPossibleModes(stack);
		for (int i = 0; i < modes.length; i++) {
			if (mode == modes[i]) {
				indexial = i;
			}
		}
		indexial ++;
		if (indexial >= modes.length) {
			indexial = 0;
		}
		String modeString = modes[indexial].id.toString();
		stack.getOrCreateTag().putString("mode", modeString);
		PacketByteBuf buffer0 = new PacketByteBuf(Unpooled.buffer());
		buffer0.writeBoolean(hand == Hand.MAIN_HAND);
		buffer0.writeString(modeString);
		((ServerPlayerEntity)context.getPlayer()).sendChatMessage(new TranslatableText(modes[indexial].getTranslationKey()), MessageType.GAME_INFO);
		//ServerSidePacketRegistry.INSTANCE.sendToPlayer(context.getPlayer(), FabricLitemod.WEAPON_MODE_UPDATE_PACKET, buffer);
	}
	
	public static void reloadPacket(PacketContext context, PacketByteBuf buffer) {
		Hand hand = buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		PlayerEntity player = context.getPlayer();
		if (!(player.getStackInHand(hand).getItem() instanceof AbstractionOfTheGun)) {
			return;
		}
		ItemStack gunneStack = player.getStackInHand(hand);
		AbstractionOfTheGun gunne = (AbstractionOfTheGun) gunneStack.getItem();
		if (getRemainingAmmo(gunneStack) > 0) {
			return;
		}
		CompoundTag tag = gunneStack.getOrCreateTag();
		for (int i = 0; i < player.inventory.getInvSize(); i++) {
			ItemStack stack = player.inventory.getInvStack(i);
			if (stack.getItem() instanceof MunitionItem) {
				MunitionType type = ((MunitionItem) stack.getItem()).type;
				int count = ((MunitionItem) stack.getItem()).getCount(stack);
				if (count == 0 || type.length > gunne.getMaxBulletLength(gunneStack) || type.diameter != gunne.getBulletDiameter(gunneStack)) {
					continue;
				}
				tag.putInt("ammo", count);
				tag.putString("ammo_type", type.name);
				stack.decrement(1);
				if (stack.getItem().hasRecipeRemainder()) {
					ItemStack remainder = new ItemStack(stack.getItem().getRecipeRemainder(), 1);
					if (!player.giveItemStack(remainder)) {
						ItemScatterer.spawn(player.world, player.getX(), player.getY(), player.getZ(), gunneStack);
					}
				}
				break;
			}
		}
		SoundEffectWrapper sound = gunne.getReloadSound(gunneStack);
		player.world.playSound(null, player.getX(), player.getY(), player.getZ(), sound.sound, SoundCategory.PLAYERS, sound.volume, sound.pitch);
	}
	
	public static class LastFireBullshite {
		
		public long lastFireTick;
		public int consecutiveShots;
		public boolean didSendAmmoWarning;
		
		public LastFireBullshite() {
			
		}
		
		public LastFireBullshite(long lastFireTick, int consecutiveShots) {
			this.lastFireTick = lastFireTick;
			this.consecutiveShots = consecutiveShots;
		}
		
	}
	
	public static class SoundEffectWrapper {
		
		public final SoundEvent sound;
		public final Identifier soundID;
		public final float volume;
		public final float pitch;
		
		public SoundEffectWrapper(Identifier soundID, float volume, float pitch) {
			this.sound = new SoundEvent(soundID);
			this.soundID = soundID;
			this.volume = volume;
			this.pitch = pitch;
		}
		
	}
	
}
