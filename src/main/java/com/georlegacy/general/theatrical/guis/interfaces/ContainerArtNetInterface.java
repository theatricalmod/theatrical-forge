package com.georlegacy.general.theatrical.guis.interfaces;

import com.georlegacy.general.theatrical.items.attr.fixture.gel.ItemGel;
import com.georlegacy.general.theatrical.tiles.interfaces.TileArtNetInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArtNetInterface extends Container {

    private TileArtNetInterface tileArtNetInterface;
    private IInventory playerInventory;

    public ContainerArtNetInterface(IInventory playerInventory, TileArtNetInterface tileArtNetInterface) {
        this.tileArtNetInterface = tileArtNetInterface;
        addPlayerSlots(playerInventory);
        this.playerInventory = playerInventory;
    }

    public IInventory getPlayerInventory() {
        return playerInventory;
    }

    private void addPlayerSlots(IInventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18,
                    170 + row * 18));
            }
        }
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 228;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemStack = null;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();
            if (itemStack1.getItem() instanceof ItemGel) {
                if (index < 1) {
                    if (!this.mergeItemStack(itemStack1, 1, this.inventorySlots.size(), true)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(itemStack1, 0, 1, false)) {
                    return null;
                }
                if (itemStack1.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }
            }
        }
        return itemStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}