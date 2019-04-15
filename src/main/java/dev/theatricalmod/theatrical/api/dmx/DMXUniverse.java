package dev.theatricalmod.theatrical.api.dmx;

import java.util.UUID;

public class DMXUniverse {

    private UUID uuid;
    private byte[] dmxChannels;

    public DMXUniverse() {
        this.uuid = UUID.randomUUID();
        this.dmxChannels = new byte[512];
    }

    public byte[] getDMXChannels(){
        return dmxChannels;
    }

    public int getChannel(int index) {
        if (index > 511 || index < 0) {
            throw new DMXValueOutOfBoundsException("There are only 512 channels in this universe");
        }
        return dmxChannels[index];
    }

    public void setChannel(int index, byte value){
        dmxChannels[index] = value;
    }

    public void setDmxChannels(byte[] data){
        dmxChannels = data;
    }

    public UUID getUuid() {
        return uuid;
    }
}
