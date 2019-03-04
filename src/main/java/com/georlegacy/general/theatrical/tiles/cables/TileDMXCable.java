package com.georlegacy.general.theatrical.tiles.cables;

import com.georlegacy.general.theatrical.api.capabilities.DMXController;
import com.georlegacy.general.theatrical.api.capabilities.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.provider.IDMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.receiver.IDMXReceiver;
import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import java.util.UUID;
import javax.annotation.Nullable;
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

    private DMXUniverse universe;
    private final IDMXReceiver idmxReceiver;
    private final IDMXProvider idmxProvider;
    private BlockPos test = new BlockPos(-255, 4, -133);

    int ticks = 0;

    public TileDMXCable() {
        this.idmxReceiver = new IDMXReceiver() {
            @Override
            public int getChannelCount() {
                return 512;
            }

            @Override
            public int getStartPoint() {
                return 0;
            }

            @Override
            public void receiveDMXValues(World world, BlockPos pos, DMXUniverse dmxUniverse) {
                if(TileDMXCable.this.universe == null){
                    TileDMXCable.this.universe = dmxUniverse;
                }else if(!TileDMXCable.this.universe.getUuid().equals(dmxUniverse.getUuid())){
                    TileDMXCable.this.universe = dmxUniverse;
                }
            }

            @Override
            public int getChannel(int index) {
                return TileDMXCable.this.universe.getChannel(index);
            }

            @Override
            public void updateChannel(int index, int value) {
                if(TileDMXCable.this.universe == null){
                    TileDMXCable.this.universe = DMXController.get(world).newUniverse();
                }
                TileDMXCable.this.universe.setChannel(index, value);
            }
        };
        this.idmxProvider = new IDMXProvider() {
            @Override
            public int[] sendDMXValues(DMXUniverse dmxUniverse) {
                if(TileDMXCable.this.universe == null){
                    TileDMXCable.this.universe = DMXController.get(world).newUniverse();
                }
                return TileDMXCable.this.universe.getDMXChannels();
            }

            @Override
            public DMXUniverse getUniverse(World world) {
                if(TileDMXCable.this.universe == null){
                    TileDMXCable.this.universe = DMXController.get(world).newUniverse();
                }
                return TileDMXCable.this.universe;
            }
        };
    }

    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
        if(this.universe != null) {
            nbtTagCompound.setString("universe", this.universe.getUuid().toString());
        }
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbtTagCompound) {
        if(nbtTagCompound.getString("universe").isEmpty()){
            return;
        }
        universe = DMXController.get(world).getUniverse(UUID.fromString(nbtTagCompound.getString("universe")));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        readNBT(compound);
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = getNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getNBT(null));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        nbtTagCompound = getNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
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
        if (capability == DMXProvider.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return DMXReceiver.CAP.cast(idmxReceiver);
        }
        if (capability == DMXProvider.CAP) {
            return DMXProvider.CAP.cast(idmxProvider);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        ticks++;
        if(ticks <= 10){
            return;
        }
        ticks = 0;
        if(this.universe == null){
            return;
        }
        for(EnumFacing facing: EnumFacing.VALUES){
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
                    receiver.receiveDMXValues(world, pos, this.universe);
                }
            }
        }
    }

    public DMXUniverse getUniverse() {
        return universe;
    }
}
