package com.robotman2412.litemod.block;

import com.robotman2412.litemod.item.FrequencyTunerItem;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;
import java.util.UUID;

public class ChannelIdentifier {
	
	public final int id;
	public final long ownerUUIDMost;
	public final long ownerUUIDLeast;
	
	public ChannelIdentifier(int id, UUID ownerUUID) {
		this.id = id;
		this.ownerUUIDMost = ownerUUID.getMostSignificantBits();
		this.ownerUUIDLeast = ownerUUID.getLeastSignificantBits();
	}
	
	public ChannelIdentifier(int id) {
		this.id = id;
		ownerUUIDLeast = 0;
		ownerUUIDMost = 0;
	}
	
	public ChannelIdentifier(CompoundTag tag) {
		id = tag.getInt("channel_number");
		if (tag.getBoolean("is_private")) {
			ownerUUIDMost = tag.getLong("owner_uuid_most");
			ownerUUIDLeast = tag.getLong("owner_uuid_least");
		}
		else
		{
			ownerUUIDLeast = 0;
			ownerUUIDMost = 0;
		}
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("is_private", ownerUUIDLeast != 0 || ownerUUIDMost != 0);
		tag.putInt("channel_number", id);
		if (ownerUUIDLeast != 0 || ownerUUIDMost != 0) {
			tag.putLong("owner_uuid_least", ownerUUIDLeast);
			tag.putLong("owner_uuid_most", ownerUUIDMost);
		}
		return tag;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChannelIdentifier)) {
			return false;
		}
		ChannelIdentifier other = (ChannelIdentifier) obj;
		return other.id == id && ownerUUIDMost == other.ownerUUIDMost && ownerUUIDLeast == other.ownerUUIDLeast;
	}
	
	/**
	 * Hash code used for <code>java.util.HashMap</code>
	 * @return A pseudo-random hash code.
	 */
	@Override
	public int hashCode() {
		long long0 = ownerUUIDLeast & 3566529374L + ownerUUIDMost * 26578337593L + id * 7569527363L;
		return new Random(long0).nextInt();
	}
	
	@Override
	public ChannelIdentifier clone() {
		return new ChannelIdentifier(id, new UUID(ownerUUIDMost, ownerUUIDLeast));
	}
	
	/**
	 * @return <code>ownerUUIDLeast</code> and <code>ownerUUIDMost</code> combined, or null if both 0
	 */
	public UUID ownerUUID() {
		if (ownerUUIDMost == 0 && ownerUUIDLeast == 0) {
			return null;
		}
		return new UUID(ownerUUIDMost, ownerUUIDLeast);
	}
	
	/**
	 * @return whether or not the channel is valid given the restrictions
	 */
	public boolean isWithinBounds() {
		if (id < 0) {
			return false;
		}
		if (ownerUUIDLeast != 0 && ownerUUIDMost != 0) {
			return id <= FrequencyTunerItem.getMaximumPrivateChannel();
		}
		return id < FrequencyTunerItem.getMaximumPublicChannel();
	}
	
}
