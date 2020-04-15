package com.robotman2412.litemod.effect;

import net.minecraft.entity.effect.StatusEffectType;

import java.awt.*;

public class HealthConfusionEffect extends StatusEffexWrapper {
	
	public HealthConfusionEffect() {
		super("health_confusion", StatusEffectType.HARMFUL, new Color(0xFFD325).getRGB());
	}
	
}
