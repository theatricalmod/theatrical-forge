package com.georlegacy.general.theatrical.api.capabilities.receiver;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class DMXReceiver implements IDMXReceiver, INBTSerializable<NBTTagCompound> {

    @CapabilityInject(IDMXReceiver.class)
    public static Capability<IDMXReceiver> CAP;

    private int dmxStartPoint;
    private int dmxChannels;
    private int[] dmxValues;

    public DMXReceiver(int dmxChannels, int dmxStartPoint){
        this.dmxChannels = dmxChannels;
        this.dmxStartPoint = dmxStartPoint;
        this.dmxValues = new int[dmxChannels];
    }

    @Override
    public int getChannelCount() {
        return dmxChannels;
    }

    @Override
    public int getStartPoint() {
        return dmxStartPoint;
    }

    public int getDMXChannel(int channel){
        return dmxValues[channel];
    }

    @Override
    public void receiveDMXValues(DMXUniverse dmxUniverse, int[] dmxChannels) {
        this.dmxValues = Arrays.copyOfRange(dmxChannels, this.dmxStartPoint, this.dmxChannels);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    public void setDmxStartPoint(int dmxStartPoint) {
        this.dmxStartPoint = dmxStartPoint;
    }

    @Override
    public int getChannel(int index) {
        if(dmxValues.length < index || dmxValues.length == 0){
            return 0;
        }
        return dmxValues[index];
    }
}
