package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import com.georlegacy.general.theatrical.api.capabilities.power.TheatricalPower;
import com.georlegacy.general.theatrical.api.fixtures.Fixture;
import com.georlegacy.general.theatrical.api.fixtures.IGelable;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockTungstenLight;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileTungstenFixture extends TileFixture implements IGelable, ITheatricalPowerStorage {


    private int ticksSinceUsage = 0;

    public int lastPower = 0;
    public int energyUsage, energyCost;

    private int power;
    private int capacity = 255;
    private int maxReceive = 255;
    private int maxExtract = 255;

    @Override
    public void setFixture(Fixture fixture) {
        super.setFixture(fixture);
        energyCost = fixture.getEnergyUse();
        energyUsage = fixture.getEnergyUseTimer();
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
//        nbtTagCompound.setTag("power", theatricalPowerStorage.serializeNBT());
        nbtTagCompound.setInteger("lastPower", lastPower);
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) nbtTagCompound.getTag("items"));
            gelType = GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
//        if (nbtTagCompound.hasKey("power")) {
//            theatricalPowerStorage.deserializeNBT(nbtTagCompound.getCompoundTag("power"));
//        }
        if (nbtTagCompound.hasKey("lastPower")) {
            lastPower = nbtTagCompound.getInteger("lastPower");
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
            return TheatricalPower.CAP.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        super.update();
        if (world.isRemote) {
            return;
        }
        if (power != lastPower) {
            lastPower = power;
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
//        ticksSinceUsage++;
//        if (ticksSinceUsage > 20) {
//            ticksSinceUsage = 0;
//            extractSocapex(getEnergyStored(), false);
//        }
    }

    @Override
    public boolean shouldTrace() {
        return power > 0;
    }

    @Override
    public boolean emitsLight() {
        return false;
    }

    @Override
    public float getIntensity() {
        return lastPower;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(this.maxReceive, maxReceive);
        if (!simulate) {
            power = energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        int energyExtracted = Math.min(power, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            power = energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return power;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    @Override
    public Class<? extends Block> getBlock() {
        return BlockTungstenLight.class;
    }
}
