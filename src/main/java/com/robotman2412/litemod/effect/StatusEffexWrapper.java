package com.robotman2412.litemod.effect;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;

public abstract class StatusEffexWrapper extends StatusEffect {
	
	public final String registryName;
	
	public StatusEffexWrapper(String registryName, StatusEffectType type, int color) {
		super(type, color);
		this.registryName = registryName;
	}
	
	public Identifier getIdentifier() {
		return new Identifier(FabricLitemod.MOD_ID, registryName);
	}
}
