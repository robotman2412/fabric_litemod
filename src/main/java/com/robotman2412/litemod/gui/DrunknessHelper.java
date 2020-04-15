package com.robotman2412.litemod.gui;

public class DrunknessHelper {
	
	public static int healthConfusion = 1;
	
	public static boolean isHeartUv(int u, int v) {
		//16, 0 -> 178, 8
		//52, 9 -> 123, 17
		//16, 45 -> 178, 53
		if (u >= 16 && u <= 178 && (v <= 8 || (v >= 45 && v <= 53))) {
				return true;
		}
		if (u >= 52 && u <= 123 && v <= 17) { //v >= 9 is always true
			return true;
		}
		return false;
	}
	
	public static class LePos {
		
		public final int x;
		public final int y;
		
		public LePos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof LePos)) {
				return false;
			}
			LePos other = (LePos) obj;
			return other.x == x && other.y == y;
		}
		
		@Override
		public int hashCode() {
			return x * 124678345 * y * -315313653;
		}
		
	}
	
	public static class HeartBit {
	
	}
	
}
