package com.robotman2412.litemod.weaopn;

import com.robotman2412.litemod.FabricLitemod;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class WeaponMode {
	
	private static Map<Identifier, WeaponMode> modeMap = new HashMap<>();
	
	public static WeaponMode SINGLE_FIRE = ensureModeRegistered(new Identifier(FabricLitemod.MOD_ID, "single_fire"), 1, true, false);
	public static WeaponMode TRI_SHOT = ensureModeRegistered(new Identifier(FabricLitemod.MOD_ID, "tri_shot"), 1, true, false);
	public static WeaponMode AUTOMATIC = ensureModeRegistered(new Identifier(FabricLitemod.MOD_ID, "automatic"), 1, false, true);
	public static WeaponMode MANUAL = ensureModeRegistered(new Identifier(FabricLitemod.MOD_ID, "manual"), 1, false, false);
	
	public final Identifier id;
	public final int shotsFired;
	public final boolean isSemiAutomatic;
	public final boolean isAutomatic;
	
	public WeaponMode(Identifier id, int shotsFired, boolean isSemiAutomatic, boolean isAutomatic) {
		this.id = id;
		this.shotsFired = shotsFired;
		this.isSemiAutomatic = isSemiAutomatic;
		this.isAutomatic = isAutomatic;
	}
	
	public static WeaponMode ensureModeRegistered(Identifier id, int shotsFired, boolean isSemiAutomatic, boolean isAutomatic) {
		WeaponMode mode = modeMap.get(id);
		if (mode == null) {
			mode = new WeaponMode(id, shotsFired, isSemiAutomatic, isAutomatic);
			modeMap.put(id, mode);
		}
		return mode;
	}
	
}
