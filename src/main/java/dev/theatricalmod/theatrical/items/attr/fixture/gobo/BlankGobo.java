package dev.theatricalmod.theatrical.items.attr.fixture.gobo;

import dev.theatricalmod.theatrical.tabs.base.CreativeTabs;
import dev.theatricalmod.theatrical.util.Reference;
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
