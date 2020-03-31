package com.robotman2412.litemod.gui;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.ChannelIdentifier;
import com.robotman2412.litemod.block.inferrer.PulseBehavior;
import com.robotman2412.litemod.item.RemoteRedstoneInferrerItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RemoteRedstoneInferrerScreen extends FrequencyTunerScreen {
	
	public ButtonWidget[] bitButtons;
	public ButtonWidget[] addBitButtons;
	public ButtonWidget[] removeBitButtons;
	public ButtonWidget incrementLength;
	public ButtonWidget decrementLength;
	
	public boolean[] bitValues;
	public int bitLength;
	
	public int minDelay;
	public int maxDelay;
	public int maxBits;
	
	public RemoteRedstoneInferrerScreen(ClientPlayerEntity user, ChannelIdentifier channel, PulseBehavior pulse, int maxBits, int minDelay, int maxDelay, Hand hand) {
		super(user, channel, hand);
		BACKGROUND = new Identifier(FabricLitemod.MOD_ID, "textures/gui/remote_redstone_inferrer.png");
		backgroundWidth = 156;
		backgroundHeight = 191;
		bitValues = new boolean[] {
				false
		};
		this.maxBits = maxBits;
		this.maxDelay = maxDelay;
		this.minDelay = minDelay;
		bitValues = pulse.pulseBits;
		bitLength = pulse.bitRedstoneTickLength;
	}
	
	@Override
	public void init(MinecraftClient client, int width, int height) {
		super.init(client, width, height);
		bitButtons = new ButtonWidget[6];
		addBitButtons = new ButtonWidget[6];
		removeBitButtons = new ButtonWidget[6];
		incrementLength = addButton(new FixedButtonWidget(0, 0, 7, 14, ">", (button0) -> {
			bitLength ++;
			decrementLength.active = true;
			if (bitLength >= 20) {
				bitLength = 20;
				button0.active = false;
			}
			sendUpdate();
		}));
		decrementLength = addButton(new FixedButtonWidget(0, 0, 7, 14, "<", (button0) -> {
			bitLength --;
			incrementLength.active = true;
			if (bitLength <= 1) {
				bitLength = 1;
				button0.active = false;
			}
			sendUpdate();
		}));
		for (int i = 0; i < 6; i++) {
			final int i0 = i;
			bitButtons[i] = addButton(new FixedButtonWidget(0, 0, 14, 14, "0", (button0) -> {
				boolean nou = !bitValues[i0];
				bitValues[i0] = nou;
				button0.setMessage(nou ? "1" : "0");
				sendUpdate();
			}));
			addBitButtons[i] = addButton(new FixedButtonWidget(0, 0, 6, 6, "+", (button0) -> addBit(i0)));
			removeBitButtons[i] = addButton(new FixedButtonWidget(0, 0, 6, 6, "-", (button0) -> removeBit(i0)));
			bitButtons[i].active = false;
			addBitButtons[i].active = false;
			removeBitButtons[i].active = false;
		}
		if (bitLength <= 1) {
			bitLength = 1;
			decrementLength.active = false;
		}
		if (bitLength >= maxDelay) {
			bitLength = maxBits;
			incrementLength.active = false;
		}
		for (int i = 0; i < 6; i++) {
			bitButtons[i].setMessage(i < bitValues.length && bitValues[i] ? "1" : "0");
			bitButtons[i].active = i < bitValues.length;
			addBitButtons[i].active = i <= bitValues.length;
			removeBitButtons[i].active = i < bitValues.length && bitValues.length > 1;
		}
	}
	
	protected void removeBit(int bit) {
		List<Boolean> list = new ArrayList<>();
		for (boolean b : bitValues) {
			list.add(b);
		}
		list.remove(bit);
		bitValues = new boolean[Math.min(list.size(), 6)];
		for (int i = 0; i < bitValues.length; i++) {
			bitValues[i] = list.get(i);
		}
		for (int i = 0; i < 6; i++) {
			bitButtons[i].setMessage(i < bitValues.length && bitValues[i] ? "1" : "0");
			bitButtons[i].active = i < bitValues.length;
			addBitButtons[i].active = i <= bitValues.length;
			removeBitButtons[i].active = i < bitValues.length && bitValues.length > 1;
		}
		sendUpdate();
	}
	
	protected void addBit(int before) {
		List<Boolean> list = new ArrayList<>();
		for (boolean b : bitValues) {
			list.add(b);
		}
		list.add(before, false);
		bitValues = new boolean[Math.min(list.size(), 6)];
		for (int i = 0; i < bitValues.length; i++) {
			bitValues[i] = list.get(i);
		}
		for (int i = 0; i < 6; i++) {
			bitButtons[i].setMessage(i < bitValues.length && bitValues[i] ? "1" : "0");
			bitButtons[i].active = i < bitValues.length;
			addBitButtons[i].active = i <= bitValues.length;
			removeBitButtons[i].active = i < bitValues.length && bitValues.length > 1;
		}
		sendUpdate();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		int offsetX = width / 2 - backgroundWidth / 2 + additionalXOffset;
		int offsetY = height / 2 - backgroundHeight / 2 + additionalYOffset;
		for (int i = 0; i < 6; i++) {
			bitButtons[i].x = offsetX + 10 + 23 * i;
			bitButtons[i].y = offsetY + 171;
			addBitButtons[i].x = offsetX + 25 + 23 * i;
			addBitButtons[i].y = offsetY + 171;
			removeBitButtons[i].x = offsetX + 25 + 23 * i;
			removeBitButtons[i].y = offsetY + 179;
		}
		decrementLength.x = offsetX + 120;
		decrementLength.y = offsetY + 153;
		incrementLength.x = offsetX + 143;
		incrementLength.y = offsetY + 153;
		super.render(mouseX, mouseY, delta);
		String text = "" + bitLength;
		int textWidth = font.getStringWidth("" + bitLength);
		font.drawWithShadow(text, offsetX + 135 - textWidth / 2f, offsetY + 156, 0xffffff);
	}
	
	@Override
	public void sendUpdate() {
		channelText = new TranslatableText("robot_litemod.gui.frequency_tuner.channel_selected").asString() + " " + (isPrivate ? selectedPrivate : selectedPublic);
		RemoteRedstoneInferrerItem.SENTPACKIDGE(player, usedHand, bitValues, bitLength, isPrivate, isPrivate ? selectedPrivate : selectedPublic);
		String sChannel = isPrivate ? "private channel " + selectedPrivate : "public channel " + selectedPublic;
		StringBuilder sTransmission = new StringBuilder();
		for (boolean b : bitValues) {
			sTransmission.append(b ? "1" : "0");
		}
		System.out.println("Updated transmission to " + sTransmission + " (left is first to send) for " + sChannel);
	}
	
}
