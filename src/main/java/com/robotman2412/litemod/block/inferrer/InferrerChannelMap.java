package com.robotman2412.litemod.block.inferrer;

import com.robotman2412.litemod.block.ChannelIdentifier;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class InferrerChannelMap {
	
	public static final InferrerChannelMap INSTANCE = new InferrerChannelMap();
	
	protected HashMap<ChannelIdentifier, HashSet<UUID>> map;
	protected List<PulseBehavior> pulses;
	protected List<PulseBehavior> pulsesToAdd;
	
	protected InferrerChannelMap() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Instance is already defined!\nPlease reference the INSTANCE constant.");
		}
		map = new HashMap<>();
		pulses = new LinkedList<>();
		pulsesToAdd = new ArrayList<>();
	}
	
	public static void tick(MinecraftServer server) {
		RedstoneInferrerBlockEntity.channels.ticked(server);
	}
	
	private void ticked(MinecraftServer server) {
		List<PulseBehavior> keep = new LinkedList<>();
		for (PulseBehavior pulse : pulses) {
			pulse.progressTicks ++;
			if (pulse.progressTicks < pulse.pulseBits.length * pulse.bitRedstoneTickLength * 2) {
				keep.add(pulse);
			}
		}
		keep.addAll(pulsesToAdd);
		pulsesToAdd = new ArrayList<>();
		pulses = keep;
	}
	
	public boolean isChannelPowered(ChannelIdentifier channel) {
		for (PulseBehavior pulse : pulses) {
			if (pulse.channelFor.equals(channel) && pulse.pulseBits[pulse.progressTicks / 2 / pulse.bitRedstoneTickLength]) {
				return true;
			}
		}
		return map.containsKey(channel);
	}
	
	public void powerChannel(ChannelIdentifier channel, boolean powered, UUID senderId) {
		HashSet<UUID> relevant = map.getOrDefault(channel, null);
		if (powered) {
			if (relevant == null) {
				relevant = new HashSet<>();
				map.put(channel, relevant);
			}
			relevant.add(senderId);
		}
		else if (relevant != null) {
			relevant.remove(senderId);
			if (relevant.size() == 0) {
				map.remove(channel);
			}
		}
	}
	
	public static boolean pulseChannel(UUID remoteID, PulseBehavior pulse) {
		pulse = pulse.clone();
		pulse.remoteID = remoteID;
		for (PulseBehavior pulse0 : INSTANCE.pulsesToAdd) {
			if (pulse0.remoteID.equals(remoteID)) {
				return false;
			}
		}
		for (PulseBehavior pulse0 : INSTANCE.pulses) {
			if (pulse0.remoteID.equals(remoteID)) {
				return false;
			}
		}
		INSTANCE.pulsesToAdd.add(pulse);
		return true;
	}
	
}
