package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.items.base.ItemCable;
import dev.theatricalmod.theatrical.tabs.base.CreativeTabs;
import dev.theatricalmod.theatrical.tiles.cables.CableType;

public class ItemSocapexCable extends ItemCable {

    public ItemSocapexCable() {
        super("socapex_cable", CableType.SOCAPEX);
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

}
