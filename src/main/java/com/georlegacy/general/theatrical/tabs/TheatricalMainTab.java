package com.georlegacy.general.theatrical.tabs;

import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class TheatricalMainTab extends CreativeTabs {

  public TheatricalMainTab() {
    super(Reference.MOD_ID);
  }

  @Override
  public ItemStack getTabIconItem() {
    return new ItemStack(Blocks.STONE);
  }
}
