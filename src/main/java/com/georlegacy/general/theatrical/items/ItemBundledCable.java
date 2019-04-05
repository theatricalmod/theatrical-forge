package com.georlegacy.general.theatrical.items;

import com.georlegacy.general.theatrical.items.base.ItemCable;
import com.georlegacy.general.theatrical.tiles.cables.CableType;

public class ItemBundledCable extends ItemCable {

    public ItemBundledCable() {
        super("bundled_cable", CableType.BUNDLED);
        this.setCreativeTab(null);
    }

}
