package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.grave.TheStoneOfGravesBlock;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	
	@Shadow @Final public PlayerContainer playerContainer;
	
	@Shadow protected abstract void vanishCursedItems();
	
	private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}
	
	@Inject(at = @At("HEAD"), method = "getHurtSound(Lnet/minecraft/entity/damage/DamageSource;)Lnet/minecraft/sound/SoundEvent;", cancellable = true)
	public void onGetHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
		if (!hasStatusEffect(FabricLitemod.HEALTH_CONFUSION)) {
			return; //do not interfere if no health confusion
		}
		if (world.random.nextInt(10) > 3) { //only 30% chance of hearing a sound
			cir.setReturnValue(null); //will play no sound
		}
	}
	
	@Inject(at = @At("HEAD"), method = "dropInventory", cancellable = true)
	public void onDropInventory(CallbackInfo ci) {
		try {
			if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
				ci.cancel();
				return;
			}
			if (!isAlive()) {
				PlayerEntity player = (PlayerEntity) (Object) this;
				vanishCursedItems();
				boolean success = TheStoneOfGravesBlock.createSuitableGrave(player, player.inventory, TheStoneOfGravesBlock.GraveType.PLAYER);
				if (success) {
					System.out.println("Inventory not dropped but put into grave.");
				} else {
					System.err.println("Inventory dropped due to no suitable grave location!");
					if (player instanceof ServerPlayerEntity) {
						Text text = new TranslatableText("death.robot_litemod.no_grave")
								.setStyle(new Style().setBold(true).setColor(Formatting.RED));
						((ServerPlayerEntity) player).sendChatMessage(text, MessageType.CHAT);
					}
					return;
				}
				ci.cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
