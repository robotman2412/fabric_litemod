package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DamageTracker.class)
public abstract class DamageTrackerMixin {
	
	@Shadow
	public abstract LivingEntity getBiggestAttacker();
	
	@Shadow @Final private List<DamageRecord> recentDamage;
	
	@Shadow @Final private LivingEntity entity;
	
	@Inject(at = @At("HEAD"), method = "getDeathMessage()Lnet/minecraft/text/Text;", cancellable = true)
	public void onGetDeathMessage(CallbackInfoReturnable<Text> cir) {
		//if you're on drugs, this'll run
		try {
			if (!(entity instanceof PlayerEntity)) {
				return;
			}
			System.out.println("The feckening.");
			PlayerEntity player = (PlayerEntity) entity;
			if (player.hasStatusEffect(FabricLitemod.HEALTH_CONFUSION)) {
				DamageRecord biggestDamage = getBiggestDamage();
				LivingEntity biggestAttacker = getBiggestAttacker();
				DamageSource biggestSource = biggestDamage.getDamageSource();
				Text outTx = null;
				Text append = null;
				if (biggestSource.isProjectile()) {
					outTx = new TranslatableText("death.robot_litemod.drug.projectile");
				} else if (biggestAttacker instanceof PlayerEntity) {
					if (biggestAttacker.hasStatusEffect(FabricLitemod.HEALTH_CONFUSION)) {
						outTx = new TranslatableText("death.robot_litemod.drug.bitchfight_lost");
					}
					else
					{
						outTx = new TranslatableText("death.robot_litemod.drug.bitchslap");
					}
				} else if (biggestSource.getAttacker() != null) {
					outTx = new TranslatableText("death.robot_litemod.drug.surprise_mob");
					append = new TranslatableText("death.robot_litemod.drug.surprise_mob.suffix");
				} else if (biggestSource == DamageSource.FALL) {
					if (player.isFallFlying()) {
						outTx = new TranslatableText("death.robot_litemod.drug.flying");
					}
					else
					{
						outTx = new TranslatableText("death.robot_litemod.drug.fall");
					}
				} else if (biggestSource == DamageSource.FLY_INTO_WALL) {
					outTx = new TranslatableText("death.robot_litemod.drug.flying");
				} else if (biggestSource == DamageSource.CACTUS) {
					outTx = new TranslatableText("death.robot_litemod.drug.cactus");
				} else if (biggestSource == DamageSource.FIREWORKS) {
					outTx = new TranslatableText("death.robot_litemod.drug.fireworks");
				} else if (biggestSource == DamageSource.LAVA) {
					outTx = new TranslatableText("death.robot_litemod.drug.lava");
				} else if (biggestSource == DamageSource.DROWN) {
					outTx = new TranslatableText("death.robot_litemod.drug.drown");
					if (biggestAttacker != null) {
						outTx.append(new TranslatableText("death.robot_litemod.escaping"));
					}
				} else if (biggestSource.isFire()) {
					outTx = new TranslatableText("death.robot_litemod.drug.fire");
					if (biggestAttacker != null) {
						outTx.append(new TranslatableText("death.robot_litemod.escaping"));
					}
				} else if (biggestSource.isExplosive()) {
					if (biggestAttacker == null) {
						outTx = new TranslatableText("death.robot_litemod.drug.explosive");
					} else {
						outTx = new TranslatableText("death.robot_litemod.drug.explosive_combat");
					}
				}
				if (biggestAttacker != null) {
					if (outTx == null) {
						outTx = new TranslatableText("death.robot_litemod.drug.surprise_mob");
						append = new TranslatableText("death.robot_litemod.drug.surprise_mob.suffix");
					}
					outTx.append(biggestAttacker.getDisplayName());
				}
				if (append != null && outTx != null) {
					outTx.append(append);
				}
				if (outTx != null) {
					if (biggestAttacker != null) {
						ItemStack stack0 = biggestAttacker.getStackInHand(Hand.MAIN_HAND);
						ItemStack stack1 = biggestAttacker.getStackInHand(Hand.OFF_HAND);
						if (!stack0.isEmpty() && (stack0.hasCustomName() || Utils.isEnchanted(stack0))) {
							outTx.append(new TranslatableText("death.robot_litemod.using_item")).append(stack0.toHoverableText());
						}
						else if (!stack1.isEmpty() && (stack0.hasCustomName() || Utils.isEnchanted(stack0))) {
							outTx.append(new TranslatableText("death.robot_litemod.using_item")).append(stack1.toHoverableText());
						}
					}
					cir.setReturnValue(player.getDisplayName().append(outTx));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public DamageRecord getBiggestDamage() {
		float mostDamage = 0;
		DamageRecord mostCause = null;
		for (DamageRecord damage : recentDamage) {
			if (damage.getDamage() > mostDamage) {
				mostDamage = damage.getDamage();
				mostCause = damage;
			}
		}
		return mostCause;
	}

}
