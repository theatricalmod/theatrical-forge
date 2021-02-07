package dev.theatricalmod.theatrical.api.capabilities.socapex;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class SocapexReceiver implements ISocapexReceiver, INBTSerializable<CompoundNBT> {

    @CapabilityInject(ISocapexReceiver.class)
    public static Capability<ISocapexReceiver> CAP;

    private int[] channels;
    private BlockPos pos;

    private List<BlockPos> blockPosList = new ArrayList<>();

    public SocapexReceiver() {
        this.channels = new int[8];
    }

    public SocapexReceiver(BlockPos pos) {
        this.channels = new int[8];
        this.pos = pos;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        for (int i = 0; i < channels.length; i++) {
            nbtTagCompound.putInt("channel_" + i, channels[i]);
        }
        nbtTagCompound.put("pos", NBTUtil.writeBlockPos(pos));
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        int[] channels = new int[8];
        for (int i = 0; i < 8; i++) {
            if (nbt.contains("channel_" + i)) {
                channels[i] = nbt.getInt("channel_" + i);
            }
        }
        if (nbt.contains("pos")) {
            pos = NBTUtil.readBlockPos(nbt.getCompound("pos"));
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
    public BlockPos getReceiverPos() {
        return pos;
    }

    public void setBlockPosList(List<BlockPos> blockPosList) {
        this.blockPosList = blockPosList;
    }

    @Override
    public List<BlockPos> getDevices() {
        return blockPosList;
    }

    @Override
    public int getTotalChannels() {
        return 0;
    }
}
