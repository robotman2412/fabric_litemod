package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.block.inferrer.RedstoneInferrerBlock;
import com.robotman2412.litemod.mixinutil.IRedstoneWireUtils;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
@Implements(@Interface(iface = IRedstoneWireUtils.class, prefix = "redu$"))
public class RedstoneWireBlockMixin {
	
	@Shadow private boolean wiresGivePower;
	
	@Inject(at = @At("HEAD"), method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", cancellable = true)
	private static void onGetconnectsTo(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
		Block block = state.getBlock();
		if (block instanceof RepeaterBlock) {
			Direction direction = state.get(RepeaterBlock.FACING);
			cir.setReturnValue(direction == dir || direction.getOpposite() == dir);
		}
		else if (block instanceof RedstoneInferrerBlock) {
			Direction direction = state.get(RedstoneInferrerBlock.FACING);
			cir.setReturnValue(direction == dir);
		}
		else if (block instanceof RedstoneWireBlock) {
			cir.setReturnValue(true);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "increasePower", cancellable = true)
	private void onIncreasePower(int power, BlockState state, CallbackInfoReturnable<Integer> cir) {
		if (state.getBlock() instanceof RedstoneWireBlock) {
			int i = state.get(RedstoneWireBlock.POWER);
			int result = Math.max(i, power);
			cir.setReturnValue(result);
		}
	}
	
	@Redirect(method = "updateLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I"))
	public int redirectUpdateLogic(World worldOn, BlockPos blockIn) {
		IRedstoneWireUtils mcWire = (IRedstoneWireUtils) Blocks.REDSTONE_WIRE;
		mcWire.setWiresGivePower(false);
		int val = worldOn.getReceivedRedstonePower(blockIn);
		mcWire.setWiresGivePower(true);
		return val;
	}
	
	public boolean redu$getWiresGivePower() {
		return wiresGivePower;
	}
	
	public void redu$setWiresGivePower(boolean value) {
		wiresGivePower = value;
	}
	
	@Inject(at = @At("HEAD"), method = "getWeakRedstonePower", cancellable = true)
	public void onGetWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing, CallbackInfoReturnable<Integer> cir) {
		IRedstoneWireUtils mcWire = (IRedstoneWireUtils) Blocks.REDSTONE_WIRE;
		if (!mcWire.getWiresGivePower()) {
			cir.setReturnValue(0);
		}
	}
	
}
