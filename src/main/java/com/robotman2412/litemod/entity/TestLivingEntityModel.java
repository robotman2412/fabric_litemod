package com.robotman2412.litemod.entity;

import com.robotman2412.litemod.entity.hyper.HyperCuboidUVMapping;
import com.robotman2412.litemod.entity.hyper.HyperModelPart;
import com.robotman2412.litemod.entity.hyper.HyperUVMapping;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.util.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TestLivingEntityModel extends CompositeEntityModel<TestLivingEntity> {
	
	private List<ModelPart> tehParts;
	
	private ModelPart body;
	private List<ModelPart> neck;
	private List<ModelPart> tail;
	private ModelPart[] tailAppendage;
	private ModelPart head;
	private ModelPart mouth;
	private ModelPart[] frontRightLeg;
	private ModelPart[] frontLeftLeg;
	private ModelPart[] backRightLeg;
	private ModelPart[] backLeftLeg;
	
	public TestLivingEntityModel() {
		construct();
	}
	
	private void construct() {
		tehParts = new ArrayList<>();
		body = new ModelPart(this, 0, 0);
		body.setTextureSize(256, 128);
		//main body
		body.addCuboid(-8, -12f, -19, 16, 15, 52);
		body.addCuboid(-7, -13f, -15, 14, 17, 30);
		//fins
		body.setTextureOffset(0, 58);
		body.addCuboid(0, -17f, -15, 0.001f, 4, 30);
		body.setTextureOffset(0, 42);
		body.addCuboid(0, -16f, 15, 0.001f, 4, 18);
		//body.addCuboid(-6, -10f, 19, 12, 12.5f, 12);
		body.setPivot(0, 0, 0);
		neck = new ArrayList<>();
		tail = new ArrayList<>();
		Vector3f angle = new Vector3f(0, 0, 0);
		Vector3f position = new Vector3f(5, 12, 0);
		Vector3f size = new Vector3f(4, 4, 4);
		float scaleFactor = 1f;
		Vector3f direction = new Vector3f(-1, 0, 0);
		Vector3f angleIncrement = new Vector3f((float) Math.PI / -64, 0, 0);
		makeTehNecc(5, 0, body, 0, -2.5f, -8);
		makeTehTail(17, 0, body, 0, -2.5f, 24);
		
		frontLeftLeg = makeLeg(7, -4.5f, -12, true);
		frontRightLeg = makeLeg(-12, -4.5f, -12, false);
		backLeftLeg = makeLeg(7, -4.5f, 24, true);
		backRightLeg = makeLeg(-12, -4.5f, 24, false);
		
		tehParts.add(frontRightLeg[0]);
		tehParts.add(frontLeftLeg[0]);
		tehParts.add(backRightLeg[0]);
		tehParts.add(backLeftLeg[0]);
		tehParts.add(body);
	}
	
	public static HyperCuboidUVMapping TAIL_UV_MAPPING_0 = new HyperCuboidUVMapping(
			new HyperUVMapping(0, 0, 11, 11), //bottom
			new HyperUVMapping(12, 0, 23, 11), //top
			new HyperUVMapping(0, 0, 11, 11), //forward
			new HyperUVMapping(0, 0, 11, 11), //backward
			new HyperUVMapping(0, 0, 11, 11), //left
			new HyperUVMapping(0, 0, 11, 11) //right
	);
	
	public void makeTehTail(int number, int depth, ModelPart parent, float posx, float posy, float posz) {
		
		TAIL_UV_MAPPING_0 = new HyperCuboidUVMapping(
				new HyperUVMapping(24, 0, 35, 11), //bottom
				new HyperUVMapping(12, 0, 23, 11), //top
				new HyperUVMapping(0, 0, 11, 11), //forward
				new HyperUVMapping(0, 0, 11, 11), //backward
				new HyperUVMapping(0, 0, 11, 11), //left
				new HyperUVMapping(0, 0, 11, 11) //right
		);
		
		if (number == 0) {
			return;
		}
		HyperModelPart part = new HyperModelPart(this, 0, 0)
				.setTextureSize(256, 128);
		parent.addChild(part);
		float cuubWidth = 12 - depth / 1.5f;// * (float) Math.pow(0.95f, depth);
		float cuubHeight = 12 - depth / 1.5f;// * (float) Math.pow(0.95f, depth);
		float cuubLength = 9;// * (float) Math.pow(0.9f, depth);
		float HA = depth == 0 ? posz : 0;
		float HA0 = depth == 0 ? posy : 0;
		part.setPivot(posx, HA0, HA + cuubLength * 0.8333334f);
		//tail segment
		part.setTextureOffset(0, 0);
		//part.addCuboid(posx - cuubWidth / 2, posy - cuubHeight / 2, 0, cuubWidth, cuubHeight, cuubLength);
		part.cube(posx - cuubWidth / 2, posy - cuubHeight / 2, 0, cuubWidth, cuubHeight, cuubLength, false, TAIL_UV_MAPPING_0);
		//fins
		//part.setTextureOffset(9, 55);
		//part.addCuboid(posx, posy - cuubHeight / 2, 0, 0.001f, -4, cuubLength);
		part.cube(posx, posy - cuubHeight / 2, 0, 0.001f, -4, cuubLength, false, TAIL_UV_MAPPING_0);
		tail.add(part);
		makeTehTail(number - 1, depth + 1, part, posx, posy, posz);
	}
	
	public HyperModelPart[] makeLeg(float x, float y, float z, boolean toeDirection) {
		HyperModelPart[] parts = new HyperModelPart[7];
		HyperModelPart upperLeg = new HyperModelPart(this)
				.setTextureSize(256, 128);
		HyperModelPart lowerLeg = new HyperModelPart(this)
				.setTextureSize(256, 128);
		HyperModelPart foot = new HyperModelPart(this)
				.setTextureSize(256, 128);
		parts[0] = upperLeg;
		parts[1] = lowerLeg;
		parts[2] = foot;
		upperLeg.setPivot(x, y, z);
		upperLeg.addCuboid(0, -2, -4, 5, 20, 8);
		upperLeg.pitch = (float) Math.PI / 5;
		upperLeg.addChild(lowerLeg);
		lowerLeg.setPivot(0, 16, 0);
		lowerLeg.addCuboid(0.5f, -2, -3, 4, 20, 6);
		lowerLeg.pitch = (float) Math.PI / -2.35f;
		lowerLeg.addChild(foot);
		foot.setPivot(0f, 18, 0);
		foot.addCuboid(0, -2, -2, 5, 3.25f, 5);
		foot.pitch = (float) Math.PI - upperLeg.pitch - lowerLeg.pitch;
		ModelPart[] toes = addToes(foot, toeDirection);
		System.arraycopy(toes, 0, parts, 3, toes.length);
		return parts;
	}
	
	public HyperModelPart[] addToes(ModelPart foot, boolean toeDirection) {
		HyperModelPart[] toeParts = new HyperModelPart[4];
		HyperModelPart toe0 = new HyperModelPart(this)
				.setTextureSize(256, 128);
		HyperModelPart toe1 = new HyperModelPart(this)
				.setTextureSize(256, 128);
		HyperModelPart toe2 = new HyperModelPart(this)
				.setTextureSize(256, 128);
		HyperModelPart sideToe = new HyperModelPart(this)
				.setTextureSize(256, 128);
		toeParts[0] = toe0;
		toeParts[1] = toe1;
		toeParts[2] = toe2;
		toeParts[3] = sideToe;
		for (HyperModelPart toePart : toeParts) {
			foot.addChild(toePart);
			toePart.addCuboid(-1f, -0.001f, 0f, 2, -2, 6.5f, toeDirection);
			toePart.addCuboid(-0.5f, -1f, 6.5f, 1, -1, 1, toeDirection);
		}
		toe0.yaw = (float) Math.PI / -16f;
		toe2.yaw = (float) Math.PI / 16f;
		toe0.setPivot(0.5f, 0 ,2);
		toe1.setPivot(2.5f, 0 ,2.2f);
		toe2.setPivot(4.5f, 0 ,2);
		sideToe.setPivot(2.5f, 0 ,0f);
		if (toeDirection) {
			sideToe.yaw = (float) Math.PI * -0.95f;
		}
		else
		{
			sideToe.yaw = (float) Math.PI * 0.95f;
		}
		return toeParts;
	}
	
	public void makeTehNecc(int number, int depth, ModelPart parent, float posx, float posy, float posz) {
		if (number == 0) {
			return;
		}
		HyperModelPart part = new HyperModelPart(this, 0, 0)
				.setTextureSize(256, 128);
		parent.addChild(part);
		float cuubWidth = 14f * (float) Math.pow(0.95f, depth);
		float cuubHeight = 12 * (float) Math.pow(0.95f, depth);
		float cuubLength = 9 * (float) Math.pow(0.9f, depth);
		float HA = depth == 0 ? posz : 0;
		float HA0 = depth == 0 ? posy : 0;
		part.setPivot(posx, HA0, HA-cuubLength * 0.8333334f);
		part.addCuboid(posx - cuubWidth / 2, posy - cuubHeight / 2, 0, cuubWidth, cuubHeight, -cuubLength);
		part.addCuboid(posx, posy - cuubHeight / 2, 0, 0.001f, -4, -cuubLength);
		neck.add(part);
		makeTehNecc(number - 1, depth + 1, part, posx, posy, posz);
	}
	
	@Override
	public Iterable<ModelPart> getParts() {
		return tehParts;
	}
	
	@Override
	public void setAngles(TestLivingEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
		construct();
		
		//headPitch = -(System.currentTimeMillis() % 3000) / 100f;
		//tehBlob.yaw = headYaw * 0.017453292F;
		for (int i = 0; i < neck.size(); i++) {
			ModelPart part = neck.get(i);
			part.pitch = (float) (Math.sin(i / (float)neck.size() * Math.PI * 2) * Math.PI / -10f) + headPitch * 0.017453292F / neck.size();
			part.yaw = headYaw * 0.017453292F / neck.size();
		}
	}
	
}
