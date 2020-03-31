package com.robotman2412.litemod.gui;

import com.robotman2412.litemod.block.ChannelIdentifier;
import com.robotman2412.litemod.block.inferrer.RedstoneInferrerBlockEntity;
import com.robotman2412.litemod.item.FrequencyTunableItem;
import com.robotman2412.litemod.item.FrequencyTunerItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class FrequencyTunerScreen extends Screen {
	
	public Identifier BACKGROUND = new Identifier("robot_litemod", "textures/gui/frequency_tuner.png");
	
	public static final TexturedButtonWidget.TexturePosition BIG_OFF = new TexturedButtonWidget.TexturePosition(190, 2, 190, 34);
	public static final TexturedButtonWidget.TexturePosition BIG_ON = new TexturedButtonWidget.TexturePosition(158, 2, 158, 34);
	public static final TexturedButtonWidget.TexturePosition BIG_SELECTED_OFF = new TexturedButtonWidget.TexturePosition(190, 66, 190, 66);
	public static final TexturedButtonWidget.TexturePosition BIG_SELECTED_ON = new TexturedButtonWidget.TexturePosition(158, 66, 158, 66);
	
	public static final TexturedButtonWidget.TexturePosition SMALL_OFF = new TexturedButtonWidget.TexturePosition(166, 98, 166, 106);
	public static final TexturedButtonWidget.TexturePosition SMALL_ON = new TexturedButtonWidget.TexturePosition(158, 98, 158, 106);
	public static final TexturedButtonWidget.TexturePosition SMALL_SELECTED_OFF = new TexturedButtonWidget.TexturePosition(166, 114, 166, 114);
	public static final TexturedButtonWidget.TexturePosition SMALL_SELECTED_ON = new TexturedButtonWidget.TexturePosition(158, 114, 158, 114);
	
	public boolean isPrivate;
	public int selectedPrivate;
	public int selectedPublic;
	public int selectedPublicPage;
	
	public ButtonWidget switchModeButton;
	public ChannelButtonWidget[] publicSelectionButtons;
	public ButtonWidget[] publicPageSelectionButtons;
	public ChannelButtonWidget[] privateSelectionButtons;
	public TextRenderer textRenderer;
	public UUID ownerUUID;
	public ClientPlayerEntity player;
	public String channelText;
	public Hand usedHand;
	
	protected int backgroundWidth = 156;
	protected int backgroundHeight = 155;
	protected int additionalXOffset = 0;
	protected int additionalYOffset = 0;
	
	public FrequencyTunerScreen(ClientPlayerEntity player, ChannelIdentifier selectedChannel, Hand hand) {
		super(new TranslatableText("robot_litemod.item.frequency_tuner"));
		this.ownerUUID = player.getUuid();
		this.player = player;
		isPrivate = selectedChannel.ownerUUID() != null;
		usedHand = hand;
		if (selectedChannel.ownerUUID() != null) {
			selectedPrivate = selectedChannel.id;
			selectedPublicPage = 0;
			selectedPublic = 1;
		}
		else
		{
			selectedPrivate = 1;
			selectedPublic = selectedChannel.id;
			selectedPublicPage = (selectedChannel.id - 1) / 256;
		}
	}
	
	@Override
	@SuppressWarnings("all")
	public void init(MinecraftClient client, int width, int height) {
		super.init(client, width, height);
		textRenderer = new TextRenderer(minecraft.getTextureManager(), new FontStorage(minecraft.getTextureManager(), new Identifier("")));
		addButton(switchModeButton = new FixedButtonWidget(10, 10, 70, 14, "", (button) -> {
			setPrivate(!isPrivate);
			sendUpdate();
		}));
		privateSelectionButtons = new ChannelButtonWidget[16];
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				final int i = (y << 2) | x;
				addButton(privateSelectionButtons[i] = new ChannelButtonWidget(0, 0, i + FrequencyTunerItem.MINIMUM_CHANNEL, true, () -> {
					selectedPrivate = i + FrequencyTunerItem.MINIMUM_CHANNEL;
					sendUpdate();
				}));
			}
		}
		publicSelectionButtons = new ChannelButtonWidget[256];
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				final int i = (y << 4) | x;
				addButton(publicSelectionButtons[i] = new ChannelButtonWidget(0, 0, i + FrequencyTunerItem.MINIMUM_CHANNEL, false, () -> {
					selectedPublic = i + FrequencyTunerItem.MINIMUM_CHANNEL + 256 * selectedPublicPage;
					sendUpdate();
				}));
			}
		}
		publicPageSelectionButtons = new ButtonWidget[4];
		for (int i = 0; i < 4; i++) {
			final int i0 = i;
			addButton(publicPageSelectionButtons[i] = new FixedButtonWidget(0, 0, 16, 30, "" + (i0 + 1), (button0) -> {
				selectedPublicPage = i0;
				button0.active = false;
				for (int x = 0; x < 4; x++) {
					if (x != i0) {
						publicPageSelectionButtons[x].active = true;
					}
				}
			}));
		}
		publicPageSelectionButtons[selectedPublicPage].active = false;
		channelText = "";
		setPrivate(isPrivate);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	@SuppressWarnings("all")
	public void render(int mouseX, int mouseY, float delta) {
		int offsetX = width / 2 - backgroundWidth / 2 + additionalXOffset;
		int offsetY = height / 2 - backgroundHeight / 2 + additionalYOffset;
		switchModeButton.x = offsetX + 6;
		switchModeButton.y = offsetY + 6;
		if (isPrivate) {
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					final int i = (y << 2) | x;
					privateSelectionButtons[i].x = offsetX + 15 + x * 32;
					privateSelectionButtons[i].y = offsetY + 23 + y * 32;
				}
			}
		}
		else
		{
			for (int y = 0; y < 16; y++) {
				for (int x = 0; x < 16; x++) {
					final int i = (y << 4) | x;
					publicSelectionButtons[i].x = offsetX + 6 + x * 8;
					publicSelectionButtons[i].y = offsetY + 23 + y * 8;
				}
			}
			for (int i = 0; i < 4; i++) {
				publicPageSelectionButtons[i].x = offsetX + 134;
				publicPageSelectionButtons[i].y = offsetY + 23 + i * 32;
			}
		}
		minecraft.getTextureManager().bindTexture(BACKGROUND);
		blit(offsetX, offsetY, 0, 0, backgroundWidth, backgroundHeight);
		super.render(mouseX, mouseY, delta);
		font.drawWithShadow(channelText, offsetX + 80, offsetY + 9, 0xffffff);
		if (!(player.getStackInHand(usedHand).getItem() instanceof FrequencyTunableItem)) { //check if item is still in hand. otherwise there is no use in keeping this open.
			player.closeScreen();
		}
	}
	
	@SuppressWarnings("all")
	public void sendUpdate() {
		System.out.println(isPrivate ? "Selected channel: private " + selectedPrivate : "Selected channel: public " + selectedPublic);
		channelText = new TranslatableText("robot_litemod.gui.frequency_tuner.channel_selected").asString() + " " + (isPrivate ? selectedPrivate : selectedPublic);
		FrequencyTunerItem.SENTPACKIDGE(isPrivate ? selectedPrivate : selectedPublic, isPrivate, usedHand);
		FrequencyTunerItem.setChannelForPlayer(MinecraftClient.getInstance().player, isPrivate ? selectedPrivate : selectedPublic, isPrivate, usedHand);
	}
	
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
		for (AbstractButtonWidget button0 : publicPageSelectionButtons) {
			button0.active = !isPrivate;
			button0.visible = !isPrivate;
		}
		for (AbstractButtonWidget button0 : publicSelectionButtons) {
			button0.active = !isPrivate;
			button0.visible = !isPrivate;
		}
		for (AbstractButtonWidget button0 : privateSelectionButtons) {
			button0.active = isPrivate;
			button0.visible = isPrivate;
		}
		publicPageSelectionButtons[selectedPublicPage].active = false;
		switchModeButton.setMessage(isPrivate ?
				new TranslatableText("robot_litemod.gui.frequency_tuner.private").asString() :
				new TranslatableText("robot_litemod.gui.frequency_tuner.public").asString()
		);
		sendUpdate();
	}
	
	class ChannelButtonWidget extends TexturedButtonWidget {
		
		boolean isBig;
		int channel;
		
		public ChannelButtonWidget(int x, int y, int channel, boolean isBig, Runnable onPress) {
			super(x, y, isBig ? 30 : 6, isBig ? 30 : 6, onPress);
			this.isBig = isBig;
			this.channel = channel;
			this.texture = BACKGROUND;
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (isSelected()) {
				return false;
			}
			return super.mouseClicked(mouseX, mouseY, button);
		}
		
		@Override
		public TexturePosition getTexturePosition() {
			int i = (isBig ? 1 : 0) | (isChannelOn() ? 2 : 0) | (isSelected() ? 4 : 0);
			switch (i) {
				case(0): //small, channel off, not selected
					return SMALL_OFF;
				case(1): //big, channel off, not selected
					return BIG_OFF;
				case(2): //small, channel on, not selected
					return SMALL_ON;
				case(3): //big, channel on, not selected
					return BIG_ON;
				case(4): //small, channel off, selected
					return SMALL_SELECTED_OFF;
				case(5): //big, channel off, selected
					return BIG_SELECTED_OFF;
				case(6): //small, channel on, selected
					return SMALL_SELECTED_ON;
				case(7): //big, channel on, selected
					return BIG_SELECTED_ON;
			}
			return super.getTexturePosition();
		}
		
		private boolean isSelected() {
			return isBig ? selectedPrivate == channel : (selectedPublic - selectedPublicPage * 256) == channel;
		}
		
		private boolean isChannelOn() {
//			if (channel == 1 && !isBig) {
//				System.out.println("Least: " + ownerUUID.getLeastSignificantBits());
//				System.out.println("Most: " + ownerUUID.getMostSignificantBits());
//			}
			ChannelIdentifier channelID = isBig ? new ChannelIdentifier(channel, ownerUUID) : new ChannelIdentifier(channel + selectedPublicPage * 256);
			return RedstoneInferrerBlockEntity.isChannelOn(channelID);
		}
		
	}
	
}
