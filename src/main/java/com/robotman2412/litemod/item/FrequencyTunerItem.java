package com.robotman2412.litemod.item;

import com.robotman2412.litemod.ClientEntry;
import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.ChannelIdentifier;
import com.robotman2412.litemod.block.inferrer.RedstoneInferrerBlockEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FrequencyTunerItem extends ItemWrapper implements FrequencyTunableItem {
	
	/** This is actually checked. */
	public static final int MINIMUM_CHANNEL = 1;
	/** Inclusive.<br>32x32 grid. */
	public static final int DEFAULT_MAXIMUM_PUBLIC_CHANNEL = 1024;
	/** Inclusive.<br>4x4 grid. */
	public static final int DEFAULT_MAXIMUM_PERSONAL_CHANNEL = 16;
	
	/**
	 * This method is here to allow server owners to add additional restrictions.
	 * Maximum channels are inclusive.
	 * @return 0 to 1024 (inclusive)
	 */
	public static int getMaximumPublicChannel() {
		return DEFAULT_MAXIMUM_PUBLIC_CHANNEL;
	}
	
	/**
	 * This method is here to allow server owners to add additional restrictions.
	 * Maximum channels are inclusive.
	 * @return 0 to 16 (inclusive)
	 */
	public static int getMaximumPrivateChannel() {
		return DEFAULT_MAXIMUM_PERSONAL_CHANNEL;
	}
	
	public FrequencyTunerItem() {
		super(new Settings().maxCount(1).group(ItemGroup.REDSTONE), "frequency_tuner");
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		ChannelIdentifier channel = getChannel(stack);
		//anti hax lol
		if (!channel.isWithinBounds()) {
			return TypedActionResult.fail(stack);
		}
		if (user.getEntityWorld().isClient()) {
			ClientEntry.openFrequencyTunerScreen(user, channel, hand);
			return TypedActionResult.pass(stack);
		}
		ServerPlayerEntity player = (ServerPlayerEntity) user;
		if (channel.ownerUUID() != null && !channel.ownerUUID().equals(player.getUuid())) {
			channel = new ChannelIdentifier(channel.id, player.getUuid());
			stack.getOrCreateTag().put("channel", channel.toTag());
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ItemStack stack = context.getStack();
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("channel")) {
			return ActionResult.FAIL;
		}
		ChannelIdentifier channel = new ChannelIdentifier(tag.getCompound("channel"));
		BlockEntity ent = context.getWorld().getBlockEntity(context.getBlockPos());
		if (ent instanceof RedstoneInferrerBlockEntity) {
			((RedstoneInferrerBlockEntity) ent).setChannel(channel);
			if (context.getPlayer() instanceof ServerPlayerEntity) {
				System.out.println("Channel set to: " + channel.toTag().toString());
				String sChannel = (channel.ownerUUID() != null ? "personal channel " : "public channel ") + channel.id;
				((ServerPlayerEntity) context.getPlayer()).sendChatMessage(new LiteralText("Channel set to " + sChannel), MessageType.GAME_INFO);
			}
			context.getWorld().updateNeighbors(context.getBlockPos(), FabricLitemod.REDSTONE_INFERRER_BLOCK);
		}
		return ActionResult.CONSUME;
	}
	
	public static ChannelIdentifier getChannel(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("channel")) {
			return new ChannelIdentifier(MINIMUM_CHANNEL);
		}
		return new ChannelIdentifier(tag.getCompound("channel"));
	}
	
	public static void setChannelForPlayer(PlayerEntity player, int channelID, boolean isPrivate, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		ChannelIdentifier channel = isPrivate ? new ChannelIdentifier(channelID, player.getUuid()) : new ChannelIdentifier(channelID);
		if (stack.getItem() instanceof FrequencyTunableItem) {
			stack.getOrCreateTag().put("channel", channel.toTag());
			player.setStackInHand(hand, stack);
			System.out.println("Channel set for player: " + player);
		}
	}
	
	/**
	 * Called by minecraft when a package is recieved.
	 */
	public static void PACKIDGE(PacketContext context, PacketByteBuf data) {
		boolean isPrivate = data.readBoolean(); //is channel private
		int id = data.readInt(); //channel number
		Hand hand = data.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND; //is held in main hand
		if (id < MINIMUM_CHANNEL) {
			return;
		}
		else if (isPrivate && id > getMaximumPrivateChannel()) {
			return;
		}
		else if (id > getMaximumPublicChannel()) {
			return;
		}
		setChannelForPlayer(context.getPlayer(), id, isPrivate, hand);
	}
	
	/**
	 * Called by the client to signal a frequency change.
	 */
	public static void SENTPACKIDGE(int channel, boolean isPrivate, Hand hand) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeBoolean(isPrivate); //is channel private
		buffer.writeInt(channel); //channel number
		buffer.writeBoolean(hand == Hand.MAIN_HAND); //is held in main hand
		ClientSidePacketRegistry.INSTANCE.sendToServer(FabricLitemod.SET_CHANNEL_PACKET, buffer);
	}
	
}
