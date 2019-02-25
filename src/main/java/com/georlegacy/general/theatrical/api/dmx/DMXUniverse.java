package com.georlegacy.general.theatrical.api.dmx;

import java.util.UUID;

public class DMXUniverse {

    private UUID uuid;
    private int[] dmxChannels;

    public DMXUniverse() {
        this.uuid = UUID.randomUUID();
        this.dmxChannels = new int[512];
    }

    public int[] getDMXChannels(){
        return dmxChannels;
    }

    public int getChannel(int index) {
        if (index > 511 || index < 0) {
            throw new DMXValueOutOfBoundsException("There are only 512 channels in this universe");
        }
        return dmxChannels[index];
    }

    public void setChannel(int index, int value){
        if(value < 0 || value > 255){
            throw new DMXValueOutOfBoundsException("Values can only be between 0 and 255");
        }
        dmxChannels[index] = value;
    }

    public UUID getUuid() {
        return uuid;
    }
}
