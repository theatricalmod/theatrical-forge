package com.georlegacy.general.theatrical.tiles.cables;

import com.georlegacy.general.theatrical.api.capabilities.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileCable extends TileEntity{

    public CableSide[] sides = new CableSide[6];

    public TileCable() {
    }

    public NBTTagCompound writeNBT(NBTTagCompound nbtTagCompound){
        for(int i = 0; i < sides.length; i++){
            if(hasSide(i)) {
                nbtTagCompound.setTag("side_" + i, sides[i].getNBT());
            }
        }
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbt){
        for(int i = 0; i < 6; i++){
            if(nbt.hasKey("side_" + i)) {
                sides[i] = CableSide.readNBT(nbt.getCompoundTag("side_" + i));
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(writeNBT(compound));
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        return writeNBT(nbtTagCompound);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
    }

    public boolean hasSide(int side){
        return sides[side] != null;
    }


    public boolean isConnected(EnumFacing enumFacing, int side, CableType typer) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(enumFacing));
        if (tileEntity == null) {
            return false;
        }
        if (tileEntity instanceof TileCable) {
            TileCable tileCable = (TileCable) tileEntity;
            if (tileCable.sides[side] != null) {
                return tileCable.sides[side].hasType(typer);
            }
            return false;
        }
        if (enumFacing == EnumFacing.EAST || enumFacing == EnumFacing.WEST || enumFacing == EnumFacing.NORTH || enumFacing == EnumFacing.SOUTH) {
            if (!hasSide(0) && !hasSide(1)) {
                return false;
            }
        } else {
            if (!hasSide(2) && !hasSide(3) && !hasSide(4) && !hasSide(5)) {
                return false;
            }
        }
        if (tileEntity.hasCapability(DMXReceiver.CAP, enumFacing.getOpposite()) || tileEntity.hasCapability(
            DMXProvider.CAP, enumFacing.getOpposite())) {
            return true;
        }
        return false;
    }

    public boolean isConnected(EnumFacing enumFacing, int side) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(enumFacing));
        if(tileEntity == null){
            return false;
        }
        if(tileEntity instanceof TileCable){
            TileCable tileCable = (TileCable)tileEntity;
            if (tileCable.sides[side] == null) {
                return false;
            }
            boolean hasType = false;
            for (CableType type : sides[side].getTypes()) {
                if (type != CableType.NONE) {
                    if (!hasType) {
                        if (tileCable.sides[side].hasType(type)) {
                            hasType = true;
                        }
                    }
                }
            }
            return hasType;
        }
        if(enumFacing == EnumFacing.EAST || enumFacing == EnumFacing.WEST || enumFacing == EnumFacing.NORTH || enumFacing == EnumFacing.SOUTH){
            if(!hasSide(0) && !hasSide(1)){
                return false;
            }
        } else {
            if(!hasSide(2) && !hasSide(3) && !hasSide(4) && !hasSide(5)){
                return false;
            }
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
            return null;
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
}
