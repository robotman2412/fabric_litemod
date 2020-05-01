package com.robotman2412.litemod.mixin;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRedstoneGateBlock.class)
public class AbstractRedstoneGateMixin {
	
	@Inject(at = @At("HEAD"), method = "getPower", cancellable = true)
	public void onGetPower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
		Direction direction = state.get(AbstractRedstoneGateBlock.FACING);
		BlockPos blockPos = pos.offset(direction);
		int i = world.getEmittedRedstonePower(blockPos, direction);
		if (i < 15) {
			BlockState blockState = world.getBlockState(blockPos);
			cir.setReturnValue(Math.max(i, blockState.getBlock() instanceof RedstoneWireBlock ? blockState.get(RedstoneWireBlock.POWER) : 0));
		}
	}
	
}
