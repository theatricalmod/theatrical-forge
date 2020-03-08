package dev.theatricalmod.theatrical.items.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.items.CableItem;

public class DMXCableItem extends CableItem {

    public DMXCableItem(Properties properties) {
        super(properties, CableType.DMX);
    }

}
