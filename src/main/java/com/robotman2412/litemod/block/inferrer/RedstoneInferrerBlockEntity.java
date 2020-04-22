package com.robotman2412.litemod.block.inferrer;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.util.RelativeDirection;
import com.robotman2412.litemod.block.ChannelIdentifier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.UUID;

public class RedstoneInferrerBlockEntity extends BlockEntity implements Tickable {
	
	protected static final InferrerChannelMap channels = InferrerChannelMap.INSTANCE;
	
	protected ChannelIdentifier channel;
	protected UUID senderId;
	protected boolean powered;
	protected boolean sending;
	
	public RedstoneInferrerBlockEntity() {
		super(FabricLitemod.REDSTONE_INFERRER_BLOCK_ENTITY);
		sending = true;
		senderId = UUID.randomUUID();
		channel = new ChannelIdentifier(1);
	}
	
	public static boolean isChannelOn(ChannelIdentifier channelID) {
		return channels.isChannelPowered(channelID);
	}
	
	@Override
	public void tick() {
		if (!sending) {
			boolean nowPowered = channels.isChannelPowered(channel);
			if (nowPowered != powered) {
				markDirty();
				powered = nowPowered;
				BlockState state = world.getBlockState(pos);
				world.setBlockState(pos, state.with(Properties.POWERED, powered));
				world.updateNeighbors(pos, state.getBlock());
				Direction dir = RelativeDirection.BACKWARD.translate(state.get(Properties.HORIZONTAL_FACING));
				BlockPos pos0 = new BlockPos(pos.getX() + dir.getOffsetX(), pos.getY(), pos.getZ() + dir.getOffsetZ());
				world.updateNeighbors(pos0, world.getBlockState(pos0).getBlock());
			}
		}
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.put("channel", channel.toTag());
		tag.putLong("sender_id_least", senderId.getLeastSignificantBits());
		tag.putLong("sender_id_most", senderId.getMostSignificantBits());
		tag.putBoolean("is_sender", sending);
		tag.putBoolean("is_powered", powered);
		return tag;
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		channel = new ChannelIdentifier(tag.getCompound("channel"));
		long most = tag.getLong("sender_id_most");
		long least = tag.getLong("sender_id_least");
		senderId = new UUID(most, least);
		sending = tag.getBoolean("is_sender");
		powered = tag.getBoolean("is_powered");
	}
	
	public void setPowered(boolean nowPowered) {
		if (nowPowered != powered && sending) {
			channels.powerChannel(channel, nowPowered, senderId);
		}
		powered = nowPowered;
	}
	
	@Override
	public boolean shouldNotCopyTagFromItem() {
		return true;
	}
	
	@Override
	public void markRemoved() {
		super.markRemoved();
		channels.powerChannel(channel, false, senderId);
	}
	
	public void setSending(boolean nowSending) {
		setPowered(false);
		sending = nowSending;
	}
	
	public boolean isSending() {
		return sending;
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	public void setChannel(ChannelIdentifier channel) {
		if (sending && powered) {
			channels.powerChannel(channel, false, senderId);
			this.channel = channel;
			channels.powerChannel(channel, powered, senderId);
		}
		else
		{
			this.channel = channel;
			// powered will update automactically in tick()
		}
	}
	
}
