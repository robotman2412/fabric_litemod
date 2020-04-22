package com.robotman2412.litemod.mixin;

import com.google.common.collect.ImmutableSet;
import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.mixinutil.IBlockColorsUtils;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(BlockColors.class)
@Implements(@Interface(iface = IBlockColorsUtils.class, prefix = "bcol$"))
public abstract class BlockColorsMixin {
	
	@Shadow protected abstract void registerColorProperties(Set<Property<?>> properties, Block... blocks);
	
	@Shadow @Final private Map<Block, Set<Property<?>>> properties;
	
	@Inject(at = @At("RETURN"), method = "create")
	private static void onCreate(CallbackInfoReturnable<BlockColors> cir) {
		BlockColors colors = cir.getReturnValue();
		IBlockColorsUtils col0 = (IBlockColorsUtils) colors; 
		col0.registerColorProperty(RedstoneWireBlock.POWER, FabricLitemod.PUSHA_REDSTONE_BLOCK);
		colors.registerColorProvider((state, view, pos, tintIndex) -> 
				RedstoneWireBlock.getWireColor(state.get(RedstoneWireBlock.POWER)),
				FabricLitemod.PUSHA_REDSTONE_BLOCK);
	}
	
	public void bcol$registerColorProperties(Set<Property<?>> properties, Block... blocks) {
		for (Block block : blocks) {
			this.properties.put(block, properties);
		}
	}
	
	public void bcol$registerColorProperty(Property<?> property, Block... blocks) {
		this.registerColorProperties(ImmutableSet.of(property), blocks);
	}
	
}
