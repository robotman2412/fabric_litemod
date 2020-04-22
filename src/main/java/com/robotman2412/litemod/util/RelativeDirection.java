package com.robotman2412.litemod.util;

import net.minecraft.util.math.Direction;

public enum RelativeDirection {
	FORWARD(0),
	LEFT(3),
	RIGHT(1),
	BACKWARD(2);
	
	/**
	 * Clockwise rotation offset to convert a horizontal direction into a direction facing this way relative to the block.
	 */
	public final int rotationOffset;
	RelativeDirection(int rotationOffset) {
		this.rotationOffset = rotationOffset;
	}
	
	public Direction translate(Direction in) {
		for (int i = 0; i < rotationOffset; i++) {
			in = in.rotateYClockwise();
		}
		return in;
	}
	
	public static RelativeDirection byRotationOffset(int offset) {
		switch (offset) {
			default:
				throw new IllegalArgumentException("Offset must be 0 to 3 inclusive!");
			case (0):
				return FORWARD;
			case (1):
				return RIGHT;
			case (2):
				return BACKWARD;
			case (3):
				return LEFT;
		}
	}
	
	public static RelativeDirection inverseTranslate(Direction from, Direction faceOf) {
		if (faceOf == Direction.UP || faceOf == Direction.DOWN) {
			return null;
		}
		if (from == Direction.UP || from == Direction.DOWN) {
			throw new IllegalArgumentException("Direction from must be horizontal!");
		}
		int i = 0;
		while (faceOf != from) {
			faceOf = faceOf.rotateYCounterclockwise();
			i ++;
		}
		return byRotationOffset(i);
	}
	
}
