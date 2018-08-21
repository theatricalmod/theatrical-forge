package com.georlegacy.general.theatrical.tabs;

import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FixturesTab extends CreativeTabs {

  public FixturesTab() {
    super(Reference.MOD_ID + "_" + "fixtures");
  }

  @Override
  public ItemStack getTabIconItem() {
    return new ItemStack(Item.getItemFromBlock(TheatricalBlocks.blockFresnel));
  }
}
