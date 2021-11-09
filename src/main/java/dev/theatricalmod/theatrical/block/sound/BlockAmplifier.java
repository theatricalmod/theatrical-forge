package dev.theatricalmod.theatrical.block.sound;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockAmplifier extends DirectionalBlock {

    public BlockAmplifier() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return super.createTileEntity(state, world);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

}
