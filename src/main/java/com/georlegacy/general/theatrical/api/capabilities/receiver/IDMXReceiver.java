package com.georlegacy.general.theatrical.api.capabilities.receiver;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;

public interface IDMXReceiver {

    int getChannelCount();

    int getStartPoint();

    void receiveDMXValues(DMXUniverse dmxUniverse, int[] dmxChannels);

    int getChannel(int index);
}
