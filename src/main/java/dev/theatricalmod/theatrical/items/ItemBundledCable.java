package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.items.base.ItemCable;
import dev.theatricalmod.theatrical.tiles.cables.CableType;

public class ItemBundledCable extends ItemCable {

    public ItemBundledCable() {
        super("bundled_cable", CableType.BUNDLED);
        this.setCreativeTab(null);
    }

}
