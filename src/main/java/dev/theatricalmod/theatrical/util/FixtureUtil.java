package dev.theatricalmod.theatrical.util;

import dev.theatricalmod.theatrical.api.fixtures.GelType;
import dev.theatricalmod.theatrical.api.fixtures.IGelable;
import dev.theatricalmod.theatrical.api.fixtures.IRGB;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityFixture;

public class FixtureUtil {

    public static int getColorFromBE(TileEntityFixture tileEntityFixture) {
        if (tileEntityFixture instanceof IGelable) {
            return ((IGelable) tileEntityFixture).getGel().getHex();
        } else if (tileEntityFixture instanceof IRGB) {
            return ((IRGB) tileEntityFixture).getColorHex();
        }
        return GelType.CLEAR.getHex();
    }

}
