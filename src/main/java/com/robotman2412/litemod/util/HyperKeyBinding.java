package com.robotman2412.litemod.util;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HyperKeyBinding extends FabricKeyBinding {
	
	public static final List<HyperKeyBinding> keys = new ArrayList<>();
	
	public boolean isPressed;
	public List<Runnable> onPress;
	public List<Runnable> onRelease;
	
	protected HyperKeyBinding(Identifier id, InputUtil.Type type, int code, String category) {
		super(id, type, code, category);
		onPress = new ArrayList<>();
		onRelease = new ArrayList<>();
		keys.add(this);
	}
	
	public void tick() {
		boolean nowPressed = isPressed();
		if (isPressed && !nowPressed) {
			onRelease.forEach(Runnable::run);
		}
		else if (!isPressed && nowPressed) {
			onPress.forEach(Runnable::run);
		}
		isPressed = nowPressed;
	}
	
	public static void tickAll(MinecraftClient minecraftClient) {
		keys.forEach(HyperKeyBinding::tick);
	}
	
	public static class Builder {
		protected final HyperKeyBinding binding;
		
		protected Builder(HyperKeyBinding binding) {
			this.binding = binding;
		}
		
		public HyperKeyBinding build() {
			return this.binding;
		}
		
		public static HyperKeyBinding.Builder create(Identifier id, InputUtil.Type type, int code, String category) {
			return new HyperKeyBinding.Builder(new HyperKeyBinding(id, type, code, category));
		}
	}
	
}
