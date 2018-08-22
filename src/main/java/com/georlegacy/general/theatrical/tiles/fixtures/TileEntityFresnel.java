package com.georlegacy.general.theatrical.tiles.fixtures;

import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityFresnel extends TileEntity {

    private GelType gelType = GelType.CLEAR;

    public void setGelType(GelType gelType){
        this.gelType = gelType;
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public GelType getGelType() {
        return gelType;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.gelType = GelType.getGelType(compound.getInteger("gelType"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("gelType", gelType.getId());
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound nbtTag = new NBTTagCompound();
        //Write your data into the nbtTag
        nbtTag.setInteger("gelType", gelType.getId());
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        NBTTagCompound tag = pkt.getNbtCompound();
        this.gelType = GelType.getGelType(tag.getInteger("gelType"));
        //Handle your Data
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState,
        IBlockState newSate) {
        return (oldState.getBlock() != newSate.getBlock());
    }
}
