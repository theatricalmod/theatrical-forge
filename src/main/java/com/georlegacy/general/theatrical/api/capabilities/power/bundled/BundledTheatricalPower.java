package com.georlegacy.general.theatrical.api.capabilities.power.bundled;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class BundledTheatricalPower implements IBundledTheatricalPowerStorage, INBTSerializable<NBTTagCompound> {

    @CapabilityInject(IBundledTheatricalPowerStorage.class)
    public static Capability<IBundledTheatricalPowerStorage> CAP;

    private int[] channels;
    private int capacityPerChannel;
    private int maxReceive;
    private int maxExtract;

    public BundledTheatricalPower(int capacityPerChannel, int maxReceive, int maxExtract) {
        this(capacityPerChannel, maxReceive, maxExtract, new int[8]);
    }

    public BundledTheatricalPower(int capacityPerChannel, int maxReceive, int maxExtract, int[] channels) {
        this.capacityPerChannel = capacityPerChannel;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.channels = channels;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        for (int i = 0; i < channels.length; i++) {
            tagCompound.setInteger("channel_" + i, channels[i]);
        }
        return tagCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        int[] channels = new int[8];
        for (int i = 0; i < 8; i++) {
            if (nbt.hasKey("channel_" + i)) {
                channels[i] = nbt.getInteger("channel_" + i);
            }
        }
        this.channels = channels;
    }

    @Override
    public int[] receiveEnergy(int[] channels, boolean simulate) {
        int[] energyReceived = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canReceive(i)) {
                energyReceived[i] = 0;
                continue;
            }
            energyReceived[i] = Math.min(capacityPerChannel - this.channels[i], Math.min(this.maxReceive, channels[i]));
            if (!simulate) {
                this.channels[i] += energyReceived[i];
            }
        }
        return energyReceived;
    }

    @Override
    public int[] extractEnergy(int[] channels, boolean simulate) {
        int[] energyExtracted = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canExtract(i)) {
                energyExtracted[i] = 0;
                continue;
            }
            energyExtracted[i] = Math.min(this.channels[i], Math.min(this.maxExtract, channels[i]));
            if (!simulate) {
                this.channels[i] -= energyExtracted[i];
            }
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(int channel) {
        return channels[channel];
    }

    @Override
    public int getMaxEnergyStored(int channel) {
        return capacityPerChannel;
    }

    @Override
    public boolean canExtract(int channel) {
        if (channels[channel] >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canReceive(int channel) {
        if (channels[channel] <= capacityPerChannel) {
            return true;
        }
        return false;
    }

    public int[] getChannels() {
        return channels;
    }
}
