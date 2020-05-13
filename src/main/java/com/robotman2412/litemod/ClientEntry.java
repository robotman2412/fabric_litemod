package com.robotman2412.litemod;

import com.robotman2412.litemod.block.BlockWrapper;
import com.robotman2412.litemod.block.ChannelIdentifier;
import com.robotman2412.litemod.block.grave.TheStoneOfGravesBlockEntityRenderer;
import com.robotman2412.litemod.block.inferrer.PulseBehavior;
import com.robotman2412.litemod.block.itemduct.ItemductBlockRenderer;
import com.robotman2412.litemod.entity.TestLivingEntityRenderer;
import com.robotman2412.litemod.foods.FoodItem;
import com.robotman2412.litemod.gui.BLENDOMATOR9000Screen;
import com.robotman2412.litemod.gui.FrequencyTunerScreen;
import com.robotman2412.litemod.gui.RemoteRedstoneInferrerScreen;
import com.robotman2412.litemod.util.HyperKeyBinding;
import com.robotman2412.litemod.weaopn.AbstractionOfTheGun;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ClientEntry implements ClientModInitializer {
	
	public static MinecraftClient client;
	
	public static boolean shouldOverrideWindowTitle = true;
	
	public static final boolean communistEasterEgg = new SecureRandom().nextDouble() < 0.01; //1% chance of getting the communist easter egg
	
	public static final long easterEggSeed = new SecureRandom().nextLong();
	
	public static final List<String> communistEasterEggSplashes = Arrays.asList(
			"\u00A7c\u00A7lPutin!",
			"\u00A7c\u00A7lStalin!",
			"\u00A7c\u00A7lUSSR!",
			"\u00A7c\u00A7lFor the people!",
			"\u00A7c\u00A7lFollow Putin on twitter or go to \u00A7kgulag\u00A7c\u00A7l!",
			"\u00A7c\u00A7lMinecraft is now Ourcraft!"
	);
	
	public static String getWindowTitle() {
		if (communistEasterEgg) {
			int variant = new Random(easterEggSeed).nextInt(5);
			switch (variant) {
				default:
				case(0):
					return "The soviet's fabric litemod - Ourcraft 1922";
				case(1):
					return "Long live the USSR! - Ourcraft 1922";
				case(2):
					return "Пролетарии всех стран, соединяйтесь!";
				case(3):
					return "Thank the great stalin - Ourcraft 1927";
				case(4):
					return "Союз Советских Социалистических Республик - Ourcraft 1922";
			}
		}
		return "RobotMan2412's fabric litemod - Minecraft " + SharedConstants.getGameVersion().getName();
	}
	
	public static FabricKeyBinding reloadWeapon;
	public static FabricKeyBinding weaponMode;
	
	public static float remainingRecoilPitch = 0;
	public static float remainingRecoilYaw = 0;
	
	public static void openFrequencyTunerScreen(PlayerEntity user, ChannelIdentifier channel, Hand hand) {
		MinecraftClient.getInstance().openScreen(new FrequencyTunerScreen((ClientPlayerEntity) user, channel, hand));
	}
	
	public static void openRemoteRedstoneInferrerScreen(PlayerEntity user, ChannelIdentifier channelFor, PulseBehavior pulse, int maxBits, int minDelay, int maxDelay, Hand hand) {
		MinecraftClient.getInstance().openScreen(new RemoteRedstoneInferrerScreen((ClientPlayerEntity) user, pulse.channelFor, pulse, maxBits, minDelay, maxDelay, hand));
	}
	
	public static void recoil(float recoilPitch, float recoilYaw) {
		remainingRecoilPitch += recoilPitch;
		remainingRecoilYaw += recoilYaw;
	}
	
	public static double getRecoilStrength() {
		return 0.3;
	}
	
	public static double recoilBackThreshold() {
		return 0.2;
	}
	
	public static boolean isClientPlayer(Entity entity) {
		return entity == client.player && entity != null;
	}
	
	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
		
		for (BlockWrapper block : FabricLitemod.ALL_BLOCKS) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, block.getRenderLayer());
		}
		for (BlockWrapper block : FoodItem.ALL_BLOCKS) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, block.getRenderLayer());
		}
		BlockRenderLayerMap.INSTANCE.putBlock(FabricLitemod.PUSHA_PEATER_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(FabricLitemod.PUSHA_PARATOR_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(FabricLitemod.PUSHA_REDSTONE_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(FabricLitemod.PUSHA_REDSTONE_TORCH_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(FabricLitemod.PUSHA_REDSTONE_WALL_TORCH_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(FabricLitemod.GOLDEN_ITEMDUCT_BLOCK, RenderLayer.getTranslucent());
		
		ScreenProviderRegistry.INSTANCE.registerFactory(FabricLitemod.BLENDOMATOR9000_CONTAINER, BLENDOMATOR9000Screen::new);
		
		EntityRendererRegistry.INSTANCE.register(FabricLitemod.TEST_LIVING_ENTITY_TYPE, TestLivingEntityRenderer::new);
		
		BlockEntityRendererRegistry.INSTANCE.register(FabricLitemod.THE_STONE_OF_GRAVES_BLOCK_ENTITY, TheStoneOfGravesBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(FabricLitemod.GOLDEN_ITEMDUCT_BLOCK_ENTITY, ItemductBlockRenderer::new);
		
		KeyBindingRegistry.INSTANCE.addCategory("weaponry");
		
		reloadWeapon = addKeybind("weaponry", "reload_weapon", GLFW.GLFW_KEY_R).onPress(() -> {
			if (client.player != null) {
				ItemStack mainHandStack = client.player.getMainHandStack();
				ItemStack offHandStack = client.player.getOffHandStack();
				if (mainHandStack.getItem() instanceof AbstractionOfTheGun) {
					AbstractionOfTheGun.sendReload(client.player, Hand.MAIN_HAND);
				}
				else if (offHandStack.getItem() instanceof AbstractionOfTheGun) {
					AbstractionOfTheGun.sendReload(client.player, Hand.OFF_HAND);
				}
			}
		});
		weaponMode = addKeybind("weaponry", "weapon_mode", GLFW.GLFW_KEY_GRAVE_ACCENT).onPress(() -> {
			if (client.player != null) {
				ItemStack mainHandStack = client.player.getMainHandStack();
				ItemStack offHandStack = client.player.getOffHandStack();
				if (mainHandStack.getItem() instanceof AbstractionOfTheGun) {
					AbstractionOfTheGun.sendSwitchMode(Hand.MAIN_HAND);
				}
				else if (offHandStack.getItem() instanceof AbstractionOfTheGun) {
					AbstractionOfTheGun.sendSwitchMode(Hand.OFF_HAND);
				}
			}
		});
		
		ClientTickCallback.EVENT.register(HyperKeyBinding::tickAll);
	}
	
	public static HyperKeyBinding addKeybind(String group, String name, int keyCode) {
		HyperKeyBinding binding = HyperKeyBinding.Builder.create(
				new Identifier(FabricLitemod.MOD_ID, name),
				InputUtil.Type.KEYSYM,
				keyCode,
				group
		).build();
		KeyBindingRegistry.INSTANCE.register(binding);
		return binding;
	}
	
}
