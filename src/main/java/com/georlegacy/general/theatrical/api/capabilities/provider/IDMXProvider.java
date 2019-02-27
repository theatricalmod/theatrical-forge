package com.georlegacy.general.theatrical.api.capabilities.provider;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;

public interface IDMXProvider {

    int[] sendDMXValues(DMXUniverse dmxUniverse);

}
