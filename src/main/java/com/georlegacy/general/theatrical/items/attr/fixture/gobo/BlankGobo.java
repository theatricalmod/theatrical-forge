package com.georlegacy.general.theatrical.items.attr.fixture.gobo;

import com.georlegacy.general.theatrical.entities.core.IHasModel;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.item.Item;

public class BlankGobo extends Item implements IHasModel {

    public BlankGobo() {
        this
                .setRegistryName(Reference.MOD_ID, "blank_gobo")
                .setUnlocalizedName("blank_gobo")
                .setMaxStackSize(16)
                .setCreativeTab(CreativeTabs.GOBOS_TAB);
    }

    @Override
    public void registerModels() {
        TheatricalItems.registerItemRenderer(this, 0, "gobo/base/blank_gobo");
    }

}
