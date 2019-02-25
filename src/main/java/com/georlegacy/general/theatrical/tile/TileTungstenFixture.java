package com.georlegacy.general.theatrical.tile;

import com.georlegacy.general.theatrical.api.IGelable;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class TileTungstenFixture extends TileFixture implements IGelable {

    private GelType gelType = GelType.CLEAR;
    private ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
            gelType = GelType.getGelType(itemStackHandler.getStackInSlot(slot).getMetadata());
        }

        @Override
        protected void onLoad() {
            markDirty();
            gelType = GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
    };

    @Override
    public GelType getGel() {
        return gelType;
    }

    @Override
    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        nbtTagCompound = super.getNBT(nbtTagCompound);
        nbtTagCompound.setTag("items", itemStackHandler.serializeNBT());
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) nbtTagCompound.getTag("items"));
            gelType = GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
        }
        return super.getCapability(capability, facing);
    }
}
