package com.robotman2412.litemod.mixinutil;

import net.minecraft.block.Block;
import net.minecraft.state.property.Property;

import java.util.Set;

public interface IBlockColorsUtils {
	
	void registerColorProperties(Set<Property<?>> properties, Block... blocks);
	
	void registerColorProperty(Property<?> property, Block... blocks);
	
}
