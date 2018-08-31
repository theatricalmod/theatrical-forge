package com.georlegacy.general.theatrical.tabs;

import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GobosTab extends CreativeTabs {

    public GobosTab() {
        super(Reference.MOD_ID + "_gobos");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(TheatricalItems.ITEM_BLANK_GOBO, 0, 0);
    }

}
