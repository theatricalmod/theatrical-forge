package com.georlegacy.general.theatrical.guis.fixtures.containers;

import com.georlegacy.general.theatrical.guis.base.SlotGel;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.Gel;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerFresnel extends Container {

    private TileEntityFresnel tileEntityFresnel;
    private IInventory playerInventory;

    public ContainerFresnel(IInventory playerInventory, TileEntityFresnel tileEntityFresnel){
        this.tileEntityFresnel = tileEntityFresnel;
        addOwnSlots();
        addPlayerSlots(playerInventory);
        this.playerInventory = playerInventory;
    }

    public IInventory getPlayerInventory() {
        return playerInventory;
    }

    private void addPlayerSlots(IInventory playerInventory){
        for(int row = 0; row < 3; ++row){
            for(int col = 0; col < 9; ++col){
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for( int row = 0; row < 9; ++row){
            int x = 8 + row * 18;
            int y = 142;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    private void addOwnSlots(){
        IItemHandler iItemHandler = this.tileEntityFresnel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new SlotGel(iItemHandler, 0, 80, 35) {
            @Override
            public void onSlotChanged() {
                tileEntityFresnel.markDirty();
            }
        });
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemStack = null;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack()){
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();
            if(itemStack1.getItem() instanceof Gel){
                if(index < 1){
                    if(!this.mergeItemStack(itemStack1, 1, this.inventorySlots.size(), true)){
                        return null;
                    }
                }else if(!this.mergeItemStack(itemStack1, 0, 1, false)){
                    return null;
                }
                if(itemStack1.isEmpty()){
                    slot.putStack(ItemStack.EMPTY);
                }else{
                    slot.onSlotChanged();
                }
            }
        }
        return itemStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileEntityFresnel.canInteractWith(playerIn);
    }


}
