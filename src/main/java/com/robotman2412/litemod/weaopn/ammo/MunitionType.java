package com.robotman2412.litemod.weaopn.ammo;

import java.util.ArrayList;
import java.util.List;

/**
 * Munition sizes rounded down!
 */
public class MunitionType {
	
	public static final MunitionType STANDARD_07_39 = ensureRegistered(new MunitionType("standard 07x39", 7, 39));
	public static final MunitionType PIERCING_07_39 = ensureRegistered(new MunitionType("armor piercing 07x39", 7, 39).piercesArmor());
	public static final MunitionType STANDARD_09_18 = ensureRegistered(new MunitionType("standard 09x18", 9, 18));
	public static final MunitionType PIERCING_09_18 = ensureRegistered(new MunitionType("armor piercing 09x18", 9, 18).piercesArmor());
	
	public static List<MunitionType> registry = new ArrayList<>();
	
	public boolean isArmorPiercing;
	public float extraKnockback;
	public float damageMultiplier;
	public final int diameter;
	public final int length;
	public final String name;
	
	public MunitionType(String name, int diameter, int length) {
		this.diameter = diameter;
		this.length = length;
		this.name = name;
	}
	
	public MunitionType piercesArmor() {
		isArmorPiercing = true;
		return this;
	}
	
	public MunitionType extraKnockback(float extraKnockback) {
		this.extraKnockback = extraKnockback;
		return this;
	}
	
	public MunitionType setDamageMultiplier(float damageMultiplier) {
		this.damageMultiplier = damageMultiplier;
		return this;
	}
	
	public static MunitionType ensureRegistered(MunitionType type) {
		if (registry == null) {
			registry = new ArrayList<>();
		}
		for (MunitionType t : registry) {
			if (t.equals(type)) {
				return t;
			}
		}
		registry.add(type);
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MunitionType)) {
			return false;
		}
		MunitionType other = (MunitionType) obj;
		return other.damageMultiplier == damageMultiplier && other.extraKnockback == extraKnockback
				&& other.diameter == diameter && other.length == length && other.isArmorPiercing == isArmorPiercing
				&& name.equals(other.name);
	}
	
}
