package com.robotman2412.litemod.mixin;

import com.robotman2412.litemod.block.railables.HyperAbstractRailBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {
	
	private AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;matches(Lnet/minecraft/tag/Tag;)Z"), method = "tick")
	public boolean redirectIsRailBlock(BlockState blockState, Tag<Block> tag) {
		boolean value = blockState.matches(tag);
		if (tag == BlockTags.RAILS) {
			return blockState.getBlock() instanceof AbstractRailBlock;
		}
		return value;
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"), method = "moveOnRail")
	public <T extends Comparable<T>> T redirectGetRailShape(BlockState state, Property<T> property) {
		T value = state.get(property);
		int x = MathHelper.floor(this.getX());
		int y = MathHelper.floor(this.getY());
		int z = MathHelper.floor(this.getZ());
		if (this.world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() instanceof AbstractRailBlock) {
			y --;
		}
		BlockPos pos = new BlockPos(x, y, z);
		if (state.getBlock() instanceof HyperAbstractRailBlock) {
			Object obj = this;
			RailShape shape = ((HyperAbstractRailBlock) state.getBlock()).getRailShapeFor(this.world, (AbstractMinecartEntity) obj, pos, state);
			try {
				return (T) shape;
			} catch (ClassCastException e) {
				return value;
			}
		}
		return value;
	}
	
}
