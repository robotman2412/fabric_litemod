package com.robotman2412.litemod.block.itemduct;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Supplier;

public class ItemDuctPartType<Type extends ItemDuctPart> {
	
	protected Supplier<Type> factory;
	protected Identifier id;
	
	protected ItemDuctPartType() {}
	
	public ItemDuctPartType(Supplier<Type> factory, Identifier id) {
		this.factory = factory;
		this.id = id;
	}
	
	public Type initialise() {
		return factory.get();
	}
	
	public Identifier getID() {
		return id;
	}
	
	public Type initialise(AbstractItemductBlockEntity entity, BlockPos pos, Direction direction) {
		Type part = initialise();
		part.targetPos = pos.offset(direction);
		part.entity = entity;
		part.targetBlockEntity = entity.getWorld().getBlockEntity(part.targetPos);
		return part;
	}
	
}
