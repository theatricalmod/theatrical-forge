package com.georlegacy.general.theatrical.util;

import com.georlegacy.general.theatrical.api.fixtures.IGelable;
import com.georlegacy.general.theatrical.api.fixtures.IRGB;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.tiles.TileFixture;

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
