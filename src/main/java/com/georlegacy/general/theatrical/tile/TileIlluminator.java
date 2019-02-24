package com.georlegacy.general.theatrical.tile;

import com.georlegacy.general.theatrical.api.IFixture;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class TileIlluminator extends TileEntity {

    private BlockPos controller;


    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound){
        if(nbtTagCompound == null){
            nbtTagCompound = new NBTTagCompound();
        }
        if(controller != null){
            nbtTagCompound.setInteger("x", controller.getX());
            nbtTagCompound.setInteger("y", controller.getY());
            nbtTagCompound.setInteger("z", controller.getZ());
        }
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbtTagCompound){
        int x = nbtTagCompound.getInteger("x");
        int y = nbtTagCompound.getInteger("y");
        int z = nbtTagCompound.getInteger("z");
        controller = new BlockPos(x, y, z);
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.readNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound = getNBT(compound);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        return new SPacketUpdateTileEntity(getPos(), 1, getNBT(null));
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
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
        this.readNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState,
        IBlockState newSate) {
        return (oldState.getBlock() != newSate.getBlock());
    }

    public BlockPos getController() {
        return controller;
    }

    public void setController(BlockPos controller) {
        this.controller = controller;
    }
}
