package com.robotman2412.litemod.mixin;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixer.schema.Schema2100;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema2100.class)
public abstract class Schema2100Mixin {
	
	@Shadow
	protected static void method_21746(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
	}
	
	@Inject(method = "registerEntities", at = @At("RETURN"))
	public void onRegisterEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
		Map<String, Supplier<TypeTemplate>> map = cir.getReturnValue();
		method_21746(schema, map, "robot_litemod:test_living_entity");
		
	}

}
