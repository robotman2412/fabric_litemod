package com.robotman2412.litemod.block.inferrer;

import com.robotman2412.litemod.block.ChannelIdentifier;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class PulseBehavior {
	
	public final boolean[] pulseBits;
	public final int bitRedstoneTickLength;
	public final ChannelIdentifier channelFor;
	public int progressTicks;
	public boolean isEffectivelyOn;
	public UUID remoteID;
	
	public PulseBehavior(boolean[] pulseBits, int bitLengthPerTick, ChannelIdentifier channelFor) {
		this.pulseBits = pulseBits;
		this.bitRedstoneTickLength = bitLengthPerTick;
		this.channelFor = channelFor;
	}
	
	public PulseBehavior(int bitLength, ChannelIdentifier channelFor) {
		bitRedstoneTickLength = bitLength;
		this.channelFor = channelFor;
		pulseBits = new boolean[] {
				true
		};
	}
	
	public PulseBehavior(CompoundTag tag) {
		bitRedstoneTickLength = tag.getInt("bit_length");
		byte[] b = tag.getByteArray("bits");
		pulseBits = new boolean[b.length];
		for (int i = 0; i < b.length; i++) {
			pulseBits[i] = b[i] != 0;
		}
		channelFor = new ChannelIdentifier(tag.getCompound("channel"));
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("bit_length", bitRedstoneTickLength);
		byte[] b = new byte[pulseBits.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (pulseBits[i] ? 1 : 0);
		}
		tag.putByteArray("bits", b);
		tag.put("channel", channelFor.toTag());
		return tag;
	}
	
	/** Does <i>not</i> respect progressTicks or remoteID. */
	@Override
	protected PulseBehavior clone() {
		return new PulseBehavior(pulseBits, bitRedstoneTickLength, channelFor);
	}
	
}
