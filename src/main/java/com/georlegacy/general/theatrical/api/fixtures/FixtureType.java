package com.georlegacy.general.theatrical.api.fixtures;

import com.georlegacy.general.theatrical.tiles.TileFixture;
import com.georlegacy.general.theatrical.tiles.TileMovingHead;
import com.georlegacy.general.theatrical.tiles.TileTungstenFixture;
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
