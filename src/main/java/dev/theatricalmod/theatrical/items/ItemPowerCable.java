package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.items.base.ItemCable;
import dev.theatricalmod.theatrical.tiles.cables.CableType;

public class ItemPowerCable extends ItemCable {

    public ItemPowerCable() {
        super("power_cable", CableType.POWER);
    }

}
