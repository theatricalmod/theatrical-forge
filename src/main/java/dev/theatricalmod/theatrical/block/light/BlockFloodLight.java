package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.fixtures.TheatricalFixtures;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockFloodLight extends BlockIntelligentFixture {

    public BlockFloodLight() {
        super(TheatricalFixtures.FLOOD_LIGHT, TheatricalBlocks.LIGHT_PROPERTIES);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileEntityIntelligentFixture tileEntityIntelligentFixture =  new TileEntityIntelligentFixture();
        tileEntityIntelligentFixture.setFixture(TheatricalFixtures.FLOOD_LIGHT);
        return tileEntityIntelligentFixture;
    }
}
