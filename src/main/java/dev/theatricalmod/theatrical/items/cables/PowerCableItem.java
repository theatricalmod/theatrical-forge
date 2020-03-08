package dev.theatricalmod.theatrical.items.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.items.CableItem;
public class PowerCableItem extends CableItem {

    public PowerCableItem(Properties properties) {
        super(properties, CableType.POWER);
    }

}
