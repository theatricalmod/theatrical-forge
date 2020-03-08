package dev.theatricalmod.theatrical.api;

import java.util.HashMap;
import java.util.Map;

public class ChannelsDefinition {

    private final Map<ChannelType, Integer> channels;

    public ChannelsDefinition() {
        this.channels = new HashMap<>();
    }

    public void setChannel(ChannelType type, int channel) {
        if (channels.containsKey(type)) {
            channels.remove(type);
        }
        channels.put(type, channel);
    }

    public int getChannel(ChannelType type) {
        return channels.getOrDefault(type, 0);
    }
}
