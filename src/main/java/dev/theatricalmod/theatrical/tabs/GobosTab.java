package dev.theatricalmod.theatrical.tabs;

import dev.theatricalmod.theatrical.init.TheatricalItems;
import dev.theatricalmod.theatrical.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class GobosTab extends CreativeTabs {

    public GobosTab() {
        super(Reference.MOD_ID + "_gobos");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(TheatricalItems.ITEM_BLANK_GOBO, 0, 0);
    }

}
