package dev.theatricalmod.theatrical.util;

import dev.theatricalmod.theatrical.api.fixtures.IGelable;
import dev.theatricalmod.theatrical.api.fixtures.IRGB;
import dev.theatricalmod.theatrical.items.attr.fixture.gel.GelType;
import dev.theatricalmod.theatrical.tiles.TileFixture;

public class FixtureUtils {

    public static int getColorFromTE(TileFixture te){
        if(te instanceof IGelable){
            return ((IGelable)te).getGel().getHex();
        }else if(te instanceof IRGB){
            return ((IRGB) te).getColorHex();
        }
        return GelType.CLEAR.getHex();
    }

}
