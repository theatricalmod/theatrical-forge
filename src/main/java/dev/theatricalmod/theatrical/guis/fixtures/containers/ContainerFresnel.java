package dev.theatricalmod.theatrical.guis.fixtures.containers;

import dev.theatricalmod.theatrical.guis.widgets.GelSlot;
import dev.theatricalmod.theatrical.tiles.TileTungstenFixture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerFresnel extends Container {

    private TileTungstenFixture tileFresnel;
    private IInventory playerInventory;

    public ContainerFresnel(IInventory playerInventory, TileTungstenFixture tileFresnel) {
        this.tileFresnel = tileFresnel;
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
        IItemHandler iItemHandler = this.tileFresnel
            .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                null);
        GelSlot gelSlot = new GelSlot(iItemHandler, 0, 80, 35) {
            @Override
            public void onSlotChanged() {
                tileFresnel.markDirty();
            }
        };
        addSlotToContainer(gelSlot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack oldStack = stack.copy();

            if (index < 1) {
                if (!mergeItemStack(stack, 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            return oldStack;
        }

        return ItemStack.EMPTY;
    }



    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileFresnel.canInteractWith(playerIn);
    }
}
