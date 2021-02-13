package dev.theatricalmod.theatrical.api;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        //Sort into ascending order of channel
        StringBuilder string = new StringBuilder();
        LinkedHashMap<ChannelType, Integer> sortedMap = new LinkedHashMap<>();
        channels.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> string.append(String.format("#%d: %s", entry.getValue(), StringUtils.capitalize(entry.getKey().name().toLowerCase()))));
        return string.toString();
    }
}
