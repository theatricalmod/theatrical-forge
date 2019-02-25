package com.georlegacy.general.theatrical.api.capabilities.provider;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import net.minecraft.nbt.NBTTagCompound;

public interface IDMXProvider {

    NBTTagCompound serialize(NBTTagCompound nbtTagCompound);

    void deserialize(NBTTagCompound nbtTagCompound);

    int[] sendDMXValues(DMXUniverse dmxUniverse);

}
