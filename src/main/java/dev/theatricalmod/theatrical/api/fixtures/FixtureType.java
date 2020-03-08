package dev.theatricalmod.theatrical.api.fixtures;

import dev.theatricalmod.theatrical.block.FixtureBlockEntity;
import dev.theatricalmod.theatrical.block.light.IntelligentFixtureBlockEntity;
import java.util.function.Supplier;

public enum FixtureType {

    INTELLIGENT(IntelligentFixtureBlockEntity::new);

    private Supplier<? extends FixtureBlockEntity> tileClass;

    FixtureType(Supplier<? extends FixtureBlockEntity> tileClass) {
        this.tileClass = tileClass;
    }

    public Supplier<? extends FixtureBlockEntity> getTileClass() {
        return this.tileClass;
    }
}
