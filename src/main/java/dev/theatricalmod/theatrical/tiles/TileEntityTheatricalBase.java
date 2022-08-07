package dev.theatricalmod.theatrical.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TileEntityTheatricalBase extends BlockEntity {

    public TileEntityTheatricalBase(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }

    public void readNBT(CompoundTag nbt){}

    public CompoundTag getNBT(CompoundTag nbt){
        if(nbt == null){
            return new CompoundTag();
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        readNBT(compound);
        super.deserializeNBT(compound);
    }

    @Override
    public void load(CompoundTag nbt) {
        readNBT(nbt);
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(getNBT(tag));
    }


    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return getNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        readNBT(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readNBT(pkt.getTag());
    }
}
