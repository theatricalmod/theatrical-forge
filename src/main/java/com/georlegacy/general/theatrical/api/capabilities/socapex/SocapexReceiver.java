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
        return new int[0];
    }

    @Override
    public int[] extractSocapex(int[] channels, boolean simulate) {
        return new int[0];
    }

    @Override
    public int getEnergyStored(int channel) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(int channel) {
        return 0;
    }

    @Override
    public boolean canExtract(int channel) {
        return false;
    }

    @Override
    public boolean canReceive(int channel) {
        return false;
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
