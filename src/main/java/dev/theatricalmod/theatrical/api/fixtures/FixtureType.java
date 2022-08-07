package dev.theatricalmod.theatrical.api.fixtures;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityFixture;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public enum FixtureType {

    INTELLIGENT(TileEntityIntelligentFixture::new, TileEntityIntelligentFixture::tick),
    TUNGSTEN(TileEntityGenericFixture::new, TileEntityGenericFixture::tick);

    private final BlockEntityType.BlockEntitySupplier<? extends TileEntityFixture> blockEntitySupplier;
    private final BlockEntityTicker<? extends TileEntityFixture> blockEntityTicker;

    FixtureType(BlockEntityType.BlockEntitySupplier<? extends TileEntityFixture> blockEntitySupplier, BlockEntityTicker<? extends TileEntityFixture> ticker) {
        this.blockEntitySupplier = blockEntitySupplier;
        this.blockEntityTicker = ticker;
    }

    public BlockEntityType.BlockEntitySupplier<? extends TileEntityFixture> getBlockEntitySupplier() {
        return this.blockEntitySupplier;
    }

    public BlockEntityTicker<? extends TileEntityFixture> getBlockEntityTicker() {
        return blockEntityTicker;
    }
}
