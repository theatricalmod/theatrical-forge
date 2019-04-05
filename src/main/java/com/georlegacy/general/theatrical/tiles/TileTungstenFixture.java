package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.IGelable;
import com.georlegacy.general.theatrical.api.capabilities.power.TheatricalPower;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class TileTungstenFixture extends TileFixture implements IGelable {

    private TheatricalPower theatricalPowerStorage;
    private int ticksSinceUsage = 0;

    public int lastPower = 0;
    public int energyUsage, energyCost;

    public TileTungstenFixture(int energyCost, int energyUsage) {
        this.theatricalPowerStorage = new TheatricalPower(255, 1000, 1000);
        this.energyCost = energyCost;
        this.energyUsage = energyUsage;
    }

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
        nbtTagCompound.setTag("power", theatricalPowerStorage.serializeNBT());
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) nbtTagCompound.getTag("items"));
            gelType = GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
        if (nbtTagCompound.hasKey("power")) {
            theatricalPowerStorage.deserializeNBT(nbtTagCompound.getCompoundTag("power"));
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        if (capability == TheatricalPower.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
        }
        if (capability == TheatricalPower.CAP) {
            return TheatricalPower.CAP.cast(theatricalPowerStorage);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        super.update();
        if (world.isRemote) {
            return;
        }
        ticksSinceUsage++;
        if (ticksSinceUsage > 10) {
            ticksSinceUsage = 0;
            lastPower = theatricalPowerStorage.getEnergyStored();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
            theatricalPowerStorage.extractEnergy(theatricalPowerStorage.getEnergyStored(), false);
        }
    }

    @Override
    public float getIntensity() {
        return theatricalPowerStorage.getEnergyStored();
    }
}
