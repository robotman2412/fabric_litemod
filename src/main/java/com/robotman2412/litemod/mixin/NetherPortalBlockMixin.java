package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetherPortalBlock.AreaHelper.class)
public abstract class NetherPortalBlockMixin {
	
	@Shadow private BlockPos lowerCorner;
	@Shadow private int width;
	@Shadow private int height;
	
	@Shadow @Final private IWorld world;
	
	@Shadow protected abstract boolean validStateInsidePortal(BlockState state);
	
	@Shadow private int foundPortalBlocks;
	
	@Shadow @Final private Direction negativeDir;
	
	@Shadow @Final private Direction positiveDir;
	
	/**
	 * @author RobotMan2412
	 */
	@Overwrite
	public boolean isValid() {
		return this.lowerCorner != null && this.width <= Utils.maxPortalWidth() && this.height <= Utils.maxPortalHeight();
	}
	
	/**
	 * @author RobotMan2412
	 */
	@Overwrite
	protected int findHeight() {
		int i;
		label56:
		for(this.height = 0; this.height < 21; ++this.height) {
			for(i = 0; i < this.width; ++i) {
				BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i).up(this.height);
				BlockState blockState = this.world.getBlockState(blockPos);
				if (!this.validStateInsidePortal(blockState)) {
					break label56;
				}
				
				Block block = blockState.getBlock();
				if (block == Blocks.NETHER_PORTAL) {
					++this.foundPortalBlocks;
				}
				
				if (i == 0) {
					block = this.world.getBlockState(blockPos.offset(this.positiveDir)).getBlock();
					if (block != Blocks.OBSIDIAN) {
						break label56;
					}
				} else if (i == this.width - 1) {
					block = this.world.getBlockState(blockPos.offset(this.negativeDir)).getBlock();
					if (block != Blocks.OBSIDIAN) {
						break label56;
					}
				}
			}
		}
		
		for(i = 0; i < this.width; ++i) {
			if (this.world.getBlockState(this.lowerCorner.offset(this.negativeDir, i).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
				this.height = 0;
				break;
			}
		}
		
		if (this.height <= 21) {
			return this.height;
		} else {
			this.lowerCorner = null;
			this.width = 0;
			this.height = 0;
			return 0;
		}
	}
	
}
