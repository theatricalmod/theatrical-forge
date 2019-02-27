package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.receiver.IDMXReceiver;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public abstract class TileDMXAcceptor extends TileFixture {


    private int channelCount;
    private int channelStartPoint;

    private final IDMXReceiver idmxReceiver;

    public TileDMXAcceptor(int channelCount, int channelStartPoint){
        this.channelCount = channelCount;
        this.channelStartPoint = channelStartPoint;
        this.idmxReceiver = new DMXReceiver(channelCount, channelStartPoint);
    }

    @Override
    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        nbtTagCompound = super.getNBT(nbtTagCompound);
        nbtTagCompound.setInteger("channelCount", channelCount);
        nbtTagCompound.setInteger("channelStartPoint", channelStartPoint);
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        channelCount = nbtTagCompound.getInteger("channelCount");
        channelStartPoint = nbtTagCompound.getInteger("channelStartPoint");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return DMXReceiver.CAP.cast(idmxReceiver);
        }
        return super.getCapability(capability, facing);
    }

}
