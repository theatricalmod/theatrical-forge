package com.georlegacy.general.theatrical.api.capabilities.receiver;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;

public class DMXReceiver implements IDMXReceiver{

    private int dmxStartPoint;
    private int dmxChannels;
    private int[] dmxValues;

    public void DMXReceiver(int dmxChannels, int dmxStartPoint){
        this.dmxChannels = dmxChannels;
        this.dmxStartPoint = dmxStartPoint;
        this.dmxValues = new int[dmxChannels];
    }

    @Override
    public NBTTagCompound serialize(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setInteger("dmxChannels", dmxChannels);
        nbtTagCompound.setInteger("dmxStartPoint", dmxStartPoint);
        return nbtTagCompound;
    }

    @Override
    public void deserialize(NBTTagCompound nbtTagCompound) {
        dmxChannels = nbtTagCompound.getInteger("dmxChannels");
        dmxStartPoint = nbtTagCompound.getInteger("dmxStartPoint");
    }

    @Override
    public int getDMXChannels() {
        return dmxChannels;
    }

    @Override
    public int getDMXStartPoint() {
        return dmxStartPoint;
    }

    public int getDMXChannel(int channel){
        return dmxValues[channel];
    }

    @Override
    public void receiveDMXValues(DMXUniverse dmxUniverse, int[] dmxChannels) {
        this.dmxValues = Arrays.copyOfRange(dmxChannels, this.dmxStartPoint, this.dmxChannels);
    }
}
