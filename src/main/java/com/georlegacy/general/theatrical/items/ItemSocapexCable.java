package com.georlegacy.general.theatrical.items;

import com.georlegacy.general.theatrical.items.base.ItemCable;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.cables.CableType;

public class ItemSocapexCable extends ItemCable {

    public ItemSocapexCable() {
        super("socapex_cable", CableType.SOCAPEX);
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

}
