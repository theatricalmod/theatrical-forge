package com.georlegacy.general.theatrical.inventory.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GelTab extends CreativeTabs {

    public GelTab() {
        super(CreativeTabs.getNextID(), "Lighting Gels");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return null;
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Lighting Gels";
    }

}
