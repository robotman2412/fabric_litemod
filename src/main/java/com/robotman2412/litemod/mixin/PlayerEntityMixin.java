package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	
	/**
	 * This is a mixin, construction is illegal.
	 * The LivingEntity class wants a constructor, tough.
	 */
	private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}
	
	/**
	 * This is a mixin, construction is illegal.
	 * The LivingEntity class wants a constructor, tough.
	 */
	private PlayerEntityMixin(World world) {super(null, world);}
	
	@Inject(at = @At("HEAD"), method = "getHurtSound(Lnet/minecraft/entity/damage/DamageSource;)Lnet/minecraft/sound/SoundEvent;", cancellable = true)
	public void onGetHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
		if (!hasStatusEffect(FabricLitemod.HEALTH_CONFUSION)) {
			return; //do not interfere if no health confusion
		}
		if (world.random.nextInt(10) > 3) { //only 30% chance of hearing a sound
			cir.setReturnValue(null); //will play no sound
		}
	}
	
}
