package com.georlegacy.general.theatrical.items.fixtureattr.gel;

import com.georlegacy.general.theatrical.TheatricalMain;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class Gel extends Item {

    public Gel() {
        this
                .setRegistryName("theatrical", "gel")
                .setUnlocalizedName("Unknown Gel")
                .setMaxStackSize(64)
                .setMaxDamage(0)
                .setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for (GelType gelType : GelType.values()) {
            itemStacks.add(new ItemStack(
                this.setUnlocalizedName(gelType.getName() + " Gel (" + gelType.getId() + ")")
                    .setCreativeTab(TheatricalMain.getTabManager().getGelTab()),
                1,
                gelType.getId()
            ));
        }
        items.addAll(itemStacks);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        GelType gelType = GelType.getGelType(stack.getMetadata());
        return gelType.getName() + " Gel (" + gelType.getId() + ")";
    }
}
