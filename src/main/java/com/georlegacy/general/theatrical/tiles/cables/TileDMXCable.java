package com.georlegacy.general.theatrical.tiles.cables;

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
            public void receiveDMXValues(byte[] data, EnumFacing facing, World world, BlockPos pos) {
                TileDMXCable.this.data = data;
                TileDMXCable.this.lastSignalFrom = facing;
                TileDMXCable.this.sendSignal();
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
        if(tileEntity.hasCapability(DMXReceiver.CAP, enumFacing.getOpposite())){
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

    public void sendSignal(){
        for(EnumFacing facing: EnumFacing.VALUES){
            if(facing == lastSignalFrom){
                continue;
            }
            BlockPos pos1 = pos.offset(facing);
            TileEntity tileEntity = world.getTileEntity(pos1);
            if(tileEntity == null){
                continue;
            } else if(tileEntity.hasCapability(DMXReceiver.CAP, facing.getOpposite())){
                if(tileEntity.getCapability(DMXReceiver.CAP, facing.getOpposite()) instanceof IDMXReceiver){
                    IDMXReceiver receiver = tileEntity.getCapability(DMXReceiver.CAP, facing.getOpposite());
                    if(receiver == null){
                        continue;
                    }
                    receiver.receiveDMXValues(data, facing.getOpposite(), world, pos1);
                    tileEntity.markDirty();
                    world.notifyBlockUpdate(pos1, world.getBlockState(pos1), world.getBlockState(pos1), 11);
                }
            }
        }
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }
        ticks++;
        if(ticks < 3){
            return;
        }
        ticks = 0;
        sendSignal();
    }

    public byte[] getData() {
        return data;
    }
}
