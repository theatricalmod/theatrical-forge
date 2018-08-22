package com.georlegacy.general.theatrical.tiles.fixtures;

import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFresnel extends TileEntity {

    private GelType gelType = GelType.CLEAR;

    public void setGelType(GelType gelType){
        this.gelType = gelType;
    }

    public GelType getGelType() {
        return gelType;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        gelType = GelType.getGelType(compound.getInteger("gel"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("gelType", gelType.getId());
        return compound;
    }
}
