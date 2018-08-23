package com.georlegacy.general.theatrical.gui;

import com.georlegacy.general.theatrical.items.attr.fixture.gel.Gel;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGel extends SlotItemHandler {


    public SlotGel(IItemHandler itemHandler, int index, int xPosition,
        int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof Gel;
    }
}
