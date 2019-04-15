package dev.theatricalmod.theatrical.guis.dimming;

import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import dev.theatricalmod.theatrical.items.attr.fixture.gel.ItemGel;
import dev.theatricalmod.theatrical.tiles.dimming.TileDimmerRack;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ContainerDimmerRack extends Container {

    private TileDimmerRack tileDimmerRack;
    private IInventory playerInventory;
    private List<ISocapexReceiver> receivers;

    public ContainerDimmerRack(IInventory playerInventory, TileDimmerRack tileDimmerRack) {
        this.tileDimmerRack = tileDimmerRack;
        addPlayerSlots(playerInventory);
        this.playerInventory = playerInventory;
        receivers = tileDimmerRack.getCapability(SocapexProvider.CAP, null).getDevices(tileDimmerRack.getWorld(), tileDimmerRack.getPos());
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

    public List<ISocapexReceiver> getDevices() {
        return receivers;
    }

    public int[] getChannelsForReceiver(ISocapexReceiver iSocapexReceiver) {
        return tileDimmerRack.getCapability(SocapexProvider.CAP, null).getPatchedCables(iSocapexReceiver);
    }

    public SocapexPatch[] getPatch(int i) {
        return tileDimmerRack.getCapability(SocapexProvider.CAP, null).getPatch(i);
    }

    public String getIdentifier(BlockPos pos) {
        TileEntity tileEntity = tileDimmerRack.getWorld().getTileEntity(pos);
        if (tileEntity != null && tileEntity.hasCapability(SocapexReceiver.CAP, null)) {
            return tileEntity.getCapability(SocapexReceiver.CAP, null).getIdentifier();
        }
        return "";
    }

}