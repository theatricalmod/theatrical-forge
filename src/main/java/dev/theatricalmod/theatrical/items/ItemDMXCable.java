package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.items.base.ItemCable;
import net.minecraft.item.Item;

public class ItemDMXCable extends ItemCable {

    public ItemDMXCable(Item.Properties properties) {
        super(properties, CableType.DMX);
    }

}
