package dev.theatricalmod.theatrical.block.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.tiles.TileEntityPowerCable;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockPowerCable extends BlockCable {

    public BlockPowerCable() {
        super(CableType.POWER);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityPowerCable();
    }
}
