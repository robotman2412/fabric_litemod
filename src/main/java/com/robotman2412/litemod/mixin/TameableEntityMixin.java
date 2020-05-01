package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.block.grave.TheStoneOfGravesBlock;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {
	
	@Inject(at = @At("RETURN"), method = "onDeath")
	public void onDeath(DamageSource source, CallbackInfo ci) {
		TameableEntity entity = (TameableEntity) (Object) this;
		if (entity.isTamed()) {
			TheStoneOfGravesBlock.createSuitableGrave(entity, null, TheStoneOfGravesBlock.GraveType.DOG);
		}
	}
	
}
