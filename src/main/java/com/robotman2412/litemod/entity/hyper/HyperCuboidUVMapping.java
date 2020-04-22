package com.robotman2412.litemod.entity.hyper;

public class HyperCuboidUVMapping {
	
	public HyperUVMapping top;
	public HyperUVMapping bottom;
	public HyperUVMapping forward;
	public HyperUVMapping backward;
	public HyperUVMapping left;
	public HyperUVMapping right;
	
	public HyperCuboidUVMapping(HyperUVMapping top, HyperUVMapping bottom, HyperUVMapping forward, HyperUVMapping backward, HyperUVMapping left, HyperUVMapping right) {
		this.top = top;
		this.bottom = bottom;
		this.forward = forward;
		this.backward = backward;
		this.left = left;
		this.right = right;
	}
	
}
