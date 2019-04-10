package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.IAcceptsCable;
import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public abstract class TileDMXAcceptor extends TileFixture implements IAcceptsCable {

    private final IDMXReceiver idmxReceiver;

    public TileDMXAcceptor() {
        this.idmxReceiver = new DMXReceiver(0, 0);
    }

    @Override
    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        nbtTagCompound = super.getNBT(nbtTagCompound);
        nbtTagCompound.setInteger("channelCount", idmxReceiver.getChannelCount());
        nbtTagCompound.setInteger("channelStartPoint", idmxReceiver.getStartPoint());
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        idmxReceiver.setChannelCount(nbtTagCompound.getInteger("channelCount"));
        idmxReceiver.setDMXStartPoint(nbtTagCompound.getInteger("channelStartPoint"));
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

    @Override
    public void invalidate()
    {
        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);

        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }

    @Override
    public CableType[] getAcceptedCables() {
        return new CableType[]{CableType.DMX, CableType.POWER};
    }

    public IDMXReceiver getIdmxReceiver() {
        return idmxReceiver;
    }
}
