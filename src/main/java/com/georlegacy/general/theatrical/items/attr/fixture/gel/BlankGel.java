package com.georlegacy.general.theatrical.items.attr.fixture.gel;

import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import net.minecraft.item.Item;

public class BlankGel extends Item {

    public BlankGel() {
        this
                .setRegistryName("theatrical", "blank_gel")
                .setUnlocalizedName("blank_gel")
                .setMaxStackSize(64).setCreativeTab(CreativeTabs.gelsTab);
    }

}
