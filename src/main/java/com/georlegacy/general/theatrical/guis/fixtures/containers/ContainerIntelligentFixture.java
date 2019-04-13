package com.georlegacy.general.theatrical.guis.fixtures.containers;

import com.georlegacy.general.theatrical.items.attr.fixture.gel.ItemGel;
import com.georlegacy.general.theatrical.tiles.TileDMXAcceptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerIntelligentFixture extends Container {

    private TileDMXAcceptor tileDMXAcceptor;
    private IInventory playerInventory;

    public ContainerIntelligentFixture(IInventory playerInventory, TileDMXAcceptor tileDMXAcceptor) {
        this.tileDMXAcceptor = tileDMXAcceptor;
        addOwnSlots();
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
                    121 + row * 18));
            }
        }
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 179;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    private void addOwnSlots() {
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileDMXAcceptor.canInteractWith(playerIn);
    }
}
