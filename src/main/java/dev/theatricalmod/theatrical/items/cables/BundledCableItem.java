package dev.theatricalmod.theatrical.items.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.items.CableItem;

public class BundledCableItem extends CableItem {

    public BundledCableItem(Properties properties) {
        super(properties, CableType.BUNDLED);
    }

}
