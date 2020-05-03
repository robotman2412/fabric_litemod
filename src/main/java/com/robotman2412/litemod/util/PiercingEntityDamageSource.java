package com.robotman2412.litemod.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;

public class PiercingEntityDamageSource extends EntityDamageSource {
	
	public PiercingEntityDamageSource(String name, LivingEntity attacker) {
		super(name, attacker);
		setBypassesArmor();
	}
	
}
