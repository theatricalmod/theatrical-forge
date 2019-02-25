package com.georlegacy.general.theatrical.api.capabilities.receiver;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import net.minecraft.nbt.NBTTagCompound;

public interface IDMXReceiver {

    NBTTagCompound serialize(NBTTagCompound nbtTagCompound);

    void deserialize(NBTTagCompound nbtTagCompound);

    int getDMXChannels();

    int getDMXStartPoint();

    void receiveDMXValues(DMXUniverse dmxUniverse, int[] dmxChannels);
}
