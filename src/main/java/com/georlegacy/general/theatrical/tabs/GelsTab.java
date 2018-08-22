package com.georlegacy.general.theatrical.tabs;

import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GelsTab extends CreativeTabs {

  public GelsTab() {
    super( Reference.MOD_ID + "_gels");
  }

  @Override
  public ItemStack getTabIconItem() {
    return new ItemStack(TheatricalItems.GEL_ITEM, 1, 79);
  }
}
