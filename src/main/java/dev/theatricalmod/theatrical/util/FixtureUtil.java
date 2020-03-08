package dev.theatricalmod.theatrical.util;

import dev.theatricalmod.theatrical.api.fixtures.GelType;
import dev.theatricalmod.theatrical.api.fixtures.IGelable;
import dev.theatricalmod.theatrical.api.fixtures.IRGB;
import dev.theatricalmod.theatrical.block.FixtureBlockEntity;

public class FixtureUtil {

    public static int getColorFromBE(FixtureBlockEntity fixtureBlockEntity){
        if(fixtureBlockEntity instanceof IGelable){
            return ((IGelable)fixtureBlockEntity).getGel().getHex();
        }else if(fixtureBlockEntity instanceof IRGB){
            return ((IRGB) fixtureBlockEntity).getColorHex();
        }
        return GelType.CLEAR.getHex();
    }

}
