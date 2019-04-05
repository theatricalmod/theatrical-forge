package com.georlegacy.general.theatrical.items;

import com.georlegacy.general.theatrical.items.base.ItemCable;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.cables.CableType;

public class ItemPatchCable extends ItemCable {

    public ItemPatchCable() {
        super("patch_cable", CableType.PATCH);
        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }

}
