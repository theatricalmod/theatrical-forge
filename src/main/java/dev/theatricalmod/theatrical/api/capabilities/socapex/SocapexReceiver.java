package dev.theatricalmod.theatrical.api.capabilities.socapex;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class SocapexReceiver implements ISocapexReceiver, INBTSerializable<CompoundTag> {

    public static Capability<ISocapexReceiver> CAP = CapabilityManager.get(new CapabilityToken<>() {});

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
    public CompoundTag serializeNBT() {
        CompoundTag nbtTagCompound = new CompoundTag();
        for (int i = 0; i < channels.length; i++) {
            nbtTagCompound.putInt("channel_" + i, channels[i]);
        }
        nbtTagCompound.put("pos", NbtUtils.writeBlockPos(pos));
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int[] channels = new int[8];
        for (int i = 0; i < 8; i++) {
            if (nbt.contains("channel_" + i)) {
                channels[i] = nbt.getInt("channel_" + i);
            }
        }
        if (nbt.contains("pos")) {
            pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
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
