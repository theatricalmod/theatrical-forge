package com.georlegacy.general.theatrical.tiles.cables;

import com.georlegacy.general.theatrical.api.capabilities.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.receiver.IDMXReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileDMXCable extends TileEntity implements ITickable {

    private byte[] data = new byte[512];
    private EnumFacing lastSignalFrom;
    private final IDMXReceiver idmxReceiver;

    int ticks = 0;

    public TileDMXCable() {
        idmxReceiver = new IDMXReceiver() {
            @Override
            public int getChannelCount() {
                return 512;
            }

            @Override
            public int getStartPoint() {
                return 0;
            }

            @Override
            public void receiveDMXValues(byte[] data, World world, BlockPos pos) {
                TileDMXCable.this.data = data;
            }

            @Override
            public byte getChannel(int index) {
                return TileDMXCable.this.data[index];
            }

            @Override
            public void updateChannel(int index, byte value) {
                TileDMXCable.this.data[index] = value;
            }

            @Override
            public void setDMXStartPoint(int dmxStartPoint) {

            }

            @Override
            public void setChannelCount(int channelCount) {

            }
        };
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, new NBTTagCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
    }

    public boolean isConnected(EnumFacing enumFacing){
        TileEntity tileEntity = world.getTileEntity(pos.offset(enumFacing));
        if(tileEntity == null){
            return false;
        }
        if(tileEntity instanceof TileDMXCable){
            return true;
        }
        if(tileEntity.hasCapability(DMXReceiver.CAP, enumFacing.getOpposite()) || tileEntity.hasCapability(
            DMXProvider.CAP, enumFacing.getOpposite())){
            return true;
        }
        return false;
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

    public byte[] getData() {
        return data;
    }

    @Override
    public void update() {

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
}
