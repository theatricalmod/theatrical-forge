package dev.theatricalmod.theatrical.tiles.rigging;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileSquareTruss extends TileEntity{


    public TileSquareTruss() {
    }

    public boolean isConnected(EnumFacing enumFacing){
        TileEntity tileEntity = world.getTileEntity(pos.offset(enumFacing));
        if(tileEntity == null){
            return false;
        }
        if(tileEntity instanceof TileSquareTruss){
          return true;
        }
        return false;
    }

}
