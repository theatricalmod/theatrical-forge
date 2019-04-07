package com.georlegacy.general.theatrical.api.capabilities.socapex;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class SocapexReceiver implements ISocapexReceiver, INBTSerializable<NBTTagCompound> {

    @CapabilityInject(ISocapexReceiver.class)
    public static Capability<ISocapexReceiver> CAP;

    private int[] channels;
    private String identifier;
    private BlockPos pos;


    public SocapexReceiver(BlockPos pos) {
        this.channels = new int[8];
        this.pos = pos;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        if (identifier != null) {
            nbtTagCompound.setString("identifier", identifier);
        }
        for (int i = 0; i < channels.length; i++) {
            nbtTagCompound.setInteger("channel_" + i, channels[i]);
        }
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("identifier")) {
            this.identifier = nbt.getString("identifier");
        }
        int[] channels = new int[8];
        for (int i = 0; i < 8; i++) {
            if (nbt.hasKey("channel_" + i)) {
                channels[i] = nbt.getInteger("channel_" + i);
            }
        }
        this.channels = channels;
    }

    @Override
    public int[] receiveSocapex(int[] channels, boolean simulate) {
        int[] energyReceived = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canReceive(i)) {
                energyReceived[i] = 0;
                continue;
            }
            energyReceived[i] = Math.min(255 - this.channels[i], Math.min(1000, channels[i]));
            if (!simulate) {
                this.channels[i] = energyReceived[i];
            }
        }
        return energyReceived;
    }

    @Override
    public int[] extractSocapex(int[] channels, boolean simulate) {
        int[] energyExtracted = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canExtract(i)) {
                energyExtracted[i] = 0;
                continue;
            }
            energyExtracted[i] = Math.min(this.channels[i], Math.min(1000, channels[i]));
            if (!simulate) {
                this.channels[i] -= energyExtracted[i];
            }
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(int channel) {
        return this.channels[channel];
    }

    @Override
    public int getMaxEnergyStored(int channel) {
        return 255;
    }

    @Override
    public boolean canExtract(int channel) {
        return true;
    }

    @Override
    public boolean canReceive(int channel) {
        return true;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void assignIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }
}
