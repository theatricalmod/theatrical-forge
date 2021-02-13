package dev.theatricalmod.theatrical.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public class TileEntityTheatricalBase extends TileEntity {

    public TileEntityTheatricalBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void readNBT(CompoundNBT nbt){}

    public CompoundNBT getNBT(CompoundNBT nbt){
        if(nbt == null){
            return new CompoundNBT();
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        readNBT(compound);
        super.deserializeNBT(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        readNBT(nbt);
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return super.write(getNBT(compound));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 0, getNBT(null));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return getNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState blockState, CompoundNBT tag) {
        super.handleUpdateTag(blockState, tag);
        readNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readNBT(pkt.getNbtCompound());
    }
}
