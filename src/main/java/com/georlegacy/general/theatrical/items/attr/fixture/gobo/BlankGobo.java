package com.georlegacy.general.theatrical.items.attr.fixture.gobo;

import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.item.Item;

public class BlankGobo extends Item {

    public BlankGobo() {
        this
            .setRegistryName(Reference.MOD_ID, "blank_gobo")
            .setTranslationKey("blank_gobo")
            .setMaxStackSize(16)
            .setCreativeTab(CreativeTabs.GOBOS_TAB);
    }

}
