package com.georlegacy.general.theatrical.items.fixtureattr.gel;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class Gel extends Item {

    private final NonNullList<ItemStack> subItems;

    public Gel() {
        subItems = NonNullList.create();
        this
                .setRegistryName("theatrical", "gel")
                .setUnlocalizedName("Unknown Gel")
                .setMaxStackSize(64)
                .setMaxDamage(0)
                .setHasSubtypes(true);

        this.getSubItems(null, subItems);

        for (GelType gelType : GelType.values()) {
            subItems.add(new ItemStack(
                    this.setUnlocalizedName(gelType.getName() + " Gel (" + gelType.getId() + ")"),
                    1,
                    gelType.getId()
            ));
        }
    }




}
