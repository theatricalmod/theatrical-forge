package com.georlegacy.general.theatrical.items.fixtureattr.gel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class Gel extends Item {

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (GelType gelType : GelType.values()) {
            items.add(new ItemStack(
                    this.setUnlocalizedName(gelType.getName() + " Gel (" + gelType.getId() + ")"),
                    1,
                    gelType.getId()
            ));
        }
    }

    public Gel() {
        this
                .setRegistryName("theatrical", "gel")
                .setUnlocalizedName("Unknown Gel")
                .setMaxStackSize(64)
                .setMaxDamage(0)
                .setHasSubtypes(true);
    }




}
