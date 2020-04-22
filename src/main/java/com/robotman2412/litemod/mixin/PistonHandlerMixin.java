package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.mixinutil.PistonUtils;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(PistonHandler.class)
public class PistonHandlerMixin {
	
	@Shadow @Final private World world;
	
	@Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0), method = "tryMove")
	public int redirectTryMoveCheck0(List<BlockPos> list) {
		double weight = 0;
		for (BlockPos pos : list) {
			weight += PistonUtils.getWeightForBlock(world, pos);
		}
		return 11 + (int) Math.round(weight - PistonUtils.pistonPushLimit);
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 1), method = "tryMove")
	public int redirectTryMoveCheck1(List<BlockPos> list) {
		double weight = 0;
		for (BlockPos pos : list) {
			weight += PistonUtils.getWeightForBlock(world, pos);
		}
		return 11 + (int) Math.round(weight - PistonUtils.pistonPushLimit);
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2), method = "tryMove")
	public int redirectTryMoveCheck2(List<BlockPos> list) {
		double weight = 0;
		for (BlockPos pos : list) {
			weight += PistonUtils.getWeightForBlock(world, pos);
		}
		return 12 + (int) Math.round(weight - PistonUtils.pistonPushLimit);
	}
	
	//TODO: sticking behaviours
	
}
