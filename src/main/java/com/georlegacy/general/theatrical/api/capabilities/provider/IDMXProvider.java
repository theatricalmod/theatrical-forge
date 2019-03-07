package com.georlegacy.general.theatrical.api.capabilities.provider;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import net.minecraft.world.World;

public interface IDMXProvider {

    byte[] sendDMXValues(DMXUniverse dmxUniverse);

    DMXUniverse getUniverse(World world);

}
