package com.robotman2412.litemod.item;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.block.ChannelIdentifier;
import com.robotman2412.litemod.block.inferrer.InferrerChannelMap;
import com.robotman2412.litemod.block.inferrer.PulseBehavior;
import com.robotman2412.litemod.gui.RemoteRedstoneInferrerScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.security.SecureRandom;
import java.util.UUID;

public class RemoteRedstoneInferrerItem extends ItemWrapper implements TransmissionTunableItem {
	
	public RemoteRedstoneInferrerItem() {
		super(new Settings().maxCount(1).group(ItemGroup.REDSTONE), "remote_redstone_inferrer");
	}
	
	public int getMaxBits(ItemStack stack) {
		return 6;
	}
	
	public int getMaxDelay(ItemStack stack) {
		return 20;
	}
	
	public int getMinDelay(ItemStack stack) {
		return 1;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		PulseBehavior pulse = getPulse(stack);
		int maxBits = ((RemoteRedstoneInferrerItem) stack.getItem()).getMaxBits(stack);
		int minDelay = ((RemoteRedstoneInferrerItem) stack.getItem()).getMinDelay(stack);
		int maxDelay = ((RemoteRedstoneInferrerItem) stack.getItem()).getMaxDelay(stack);
		if (user.isSneaking()) {
			if (user instanceof ClientPlayerEntity) {
				MinecraftClient.getInstance().openScreen(new RemoteRedstoneInferrerScreen((ClientPlayerEntity) user, pulse.channelFor, pulse, maxBits, minDelay, maxDelay, hand));
				return TypedActionResult.pass(stack);
			}
		}
		else
		{
			used(user.getStackInHand(hand), user);
		}
		return TypedActionResult.consume(user.getStackInHand(hand));
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		used(context.getStack(), context.getPlayer());
		return ActionResult.CONSUME;
	}
	
	@Override
	public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		used(stack, user);
		return true;
	}
	
	@SuppressWarnings("all")
	public static PulseBehavior getPulse(ItemStack stack) {
		if (!stack.getOrCreateTag().contains("transmission")) {
			return new PulseBehavior(1, new ChannelIdentifier(1));
		}
		return new PulseBehavior(stack.getSubTag("transmission"));
	}
	
	public UUID ensureRemoteID(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID senderID;
		if (!tag.contains("remote_id_least") || !tag.contains("remote_id_most")) {
			SecureRandom rand = new SecureRandom();
			senderID = new UUID(rand.nextLong(), rand.nextLong());
			tag.putLong("remote_id_most", senderID.getMostSignificantBits());
			tag.putLong("remote_id_least", senderID.getLeastSignificantBits());
		}
		else
		{
			senderID = new UUID(tag.getLong("remote_id_most"), tag.getLong("remote_id_least"));
		}
		return senderID;
	}
	
	protected void used(ItemStack stack, PlayerEntity user) {
		boolean success = InferrerChannelMap.pulseChannel(ensureRemoteID(stack), getPulse(stack));
		if (user instanceof ClientPlayerEntity && success) {
			user.playSound(new SoundEvent(new Identifier("entity.enderman.teleport")), 1, 1);
		}
	}
	
	public static void setTransmissionForPlayer(PlayerEntity player, Hand hand, int channelID, boolean isPrivate, boolean[] pulseBits, int pulseLength) {
		ItemStack stack = player.getStackInHand(hand);
		if (!(stack.getItem() instanceof TransmissionTunableItem)) {
			return;
		}
		ChannelIdentifier channel = isPrivate ? new ChannelIdentifier(channelID, player.getUuid()) : new ChannelIdentifier(channelID);
		PulseBehavior pulse = new PulseBehavior(pulseBits, pulseLength, channel);
		stack.getOrCreateTag().put("transmission", pulse.toTag());
		player.setStackInHand(hand, stack);
	}
	
	public static void PACKIDGE(PacketContext context, PacketByteBuf data) {
		boolean isPrivate = data.readBoolean(); //is channel private
		int id = data.readInt(); //channel number
		Hand hand = data.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND; //is held in main hand
		if (id < FrequencyTunerItem.MINIMUM_CHANNEL) {
			return;
		}
		else if (isPrivate && id > FrequencyTunerItem.MAXIMUM_PERSONAL_CHANNEL) {
			return;
		}
		else if (id > FrequencyTunerItem.MAXIMUM_PUBLIC_CHANNEL) {
			return;
		}
		int pulseLength = data.readInt(); //redstone tick transmission bit duration
		byte[] b = data.readByteArray(); //transmission bits
		boolean[] pulseBits = new boolean[b.length];
		for (int i = 0; i < b.length; i++) {
			pulseBits[i] = b[i] != 0;
		}
		setTransmissionForPlayer(context.getPlayer(), hand, id, isPrivate, pulseBits, pulseLength);
		System.out.println("Recieved a package.");
	}
	
	public static void SENTPACKIDGE(PlayerEntity player, Hand hand, boolean[] pulseBits, int pulseLength, boolean isPrivate, int selectedChannel) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeBoolean(isPrivate); //is channel private
		buffer.writeInt(selectedChannel); //channel number
		buffer.writeBoolean(hand == Hand.MAIN_HAND); //is held in main hand
		buffer.writeInt(pulseLength); //redstone tick transmission bit duration
		byte[] b = new byte[pulseBits.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (pulseBits[i] ? 1 : 0);
		}
		buffer.writeByteArray(b); //transmission bits
		ClientSidePacketRegistry.INSTANCE.sendToServer(FabricLitemod.SET_TRANSMISSION_PACKET, buffer);
		setTransmissionForPlayer(player, hand, selectedChannel, isPrivate, pulseBits, pulseLength);
		System.out.println("Sent a package.");
	}
	
}
