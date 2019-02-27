package com.georlegacy.general.theatrical.tiles.cables;

import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileDMXCable extends TileEntity {

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
}
