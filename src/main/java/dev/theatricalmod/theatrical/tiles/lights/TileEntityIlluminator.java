package dev.theatricalmod.theatrical.tiles.lights;

import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class TileEntityIlluminator extends TileEntity implements ITickableTileEntity {

    public TileEntityIlluminator() {
        super(TheatricalTiles.ILLUMINATOR.get());
    }

    private BlockPos controller;


    public CompoundNBT getNBT(@Nullable CompoundNBT nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new CompoundNBT();
        }
        if (controller != null) {
            nbtTagCompound.putInt("controllerX", controller.getX());
            nbtTagCompound.putInt("controllerY", controller.getY());
            nbtTagCompound.putInt("controllerZ", controller.getZ());
        }
        return nbtTagCompound;
    }

    public void readNBT(CompoundNBT nbtTagCompound) {
        int x = nbtTagCompound.getInt("controllerX");
        int y = nbtTagCompound.getInt("controllerY");
        int z = nbtTagCompound.getInt("controllerZ");
        controller = new BlockPos(x, y, z);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        readNBT(nbt);
        super.deserializeNBT(nbt);
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
        CompoundNBT nbtTagCompound = super.getUpdateTag();
        nbtTagCompound = getNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        readNBT(tag);
    }

    public BlockPos getController() {
        return controller;
    }

    public void setController(BlockPos controller) {
        this.controller = controller;
        this.markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (controller != null) {
                TileEntity tileEntity = world.getTileEntity(controller);
                if (tileEntity instanceof TileEntityFixture) {
                    TileEntityFixture fixture = (TileEntityFixture) tileEntity;
                    if (!pos.equals(fixture.getLightBlock())) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }
}
