package dev.theatricalmod.theatrical.api.fixtures;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityFixture;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;

import java.util.function.Supplier;

public enum FixtureType {

    INTELLIGENT(TileEntityIntelligentFixture::new),
    TUNGSTEN(TileEntityGenericFixture::new);

    private final Supplier<? extends TileEntityFixture> tileClass;

    FixtureType(Supplier<? extends TileEntityFixture> tileClass) {
        this.tileClass = tileClass;
    }

    public Supplier<? extends TileEntityFixture> getTileClass() {
        return this.tileClass;
    }
}
