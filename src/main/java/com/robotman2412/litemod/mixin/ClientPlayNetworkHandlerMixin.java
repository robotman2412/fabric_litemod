package com.robotman2412.litemod.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	
	@Shadow private MinecraftClient client;
	
	@Inject(at = @At("RETURN"), method = "onBlockEntityUpdate")
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
		if (this.client.world.isChunkLoaded(packet.getPos())) {
			BlockEntity blockEntity = this.client.world.getBlockEntity(packet.getPos());
			int rawId = packet.getBlockEntityType();
			BlockEntityType type = Registry.BLOCK_ENTITY_TYPE.get(rawId);
			if (blockEntity.getType() == type) {
				blockEntity.fromTag(packet.getCompoundTag());
			}
		}
	}
	
}
