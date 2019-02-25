package com.georlegacy.general.theatrical.api.capabilities.provider;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import net.minecraft.nbt.NBTTagCompound;

public class DMXProvider implements IDMXProvider {

    private DMXUniverse dmxUniverse;

    public DMXProvider(DMXUniverse dmxUniverse){
        this.dmxUniverse = dmxUniverse;
    }

    @Override
    public NBTTagCompound serialize(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setString("universe", dmxUniverse.getUuid().toString());
        return null;
    }

    @Override
    public void deserialize(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public int[] sendDMXValues(DMXUniverse dmxUniverse) {
        return dmxUniverse.getDMXChannels();
    }
}
