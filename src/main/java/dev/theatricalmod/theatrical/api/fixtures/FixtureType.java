package dev.theatricalmod.theatrical.api.fixtures;

import dev.theatricalmod.theatrical.tiles.TileFixture;
import dev.theatricalmod.theatrical.tiles.TileMovingHead;
import dev.theatricalmod.theatrical.tiles.TileTungstenFixture;
import java.util.function.Supplier;

public enum FixtureType {

    INTELLIGENT(TileMovingHead::new),
    TUNGSTEN(TileTungstenFixture::new);

    private Supplier<? extends TileFixture> tileClass;

    FixtureType(Supplier<? extends TileFixture> tileClass) {
        this.tileClass = tileClass;
    }

    public Supplier<? extends TileFixture> getTileClass() {
        return this.tileClass;
    }
}
