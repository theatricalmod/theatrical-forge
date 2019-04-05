package com.georlegacy.general.theatrical.tiles.rigging;

import com.georlegacy.general.theatrical.api.capabilities.WorldPipePanelNetwork;
import com.georlegacy.general.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import com.georlegacy.general.theatrical.api.capabilities.power.TheatricalPower;
import com.georlegacy.general.theatrical.tiles.TileBase;
import com.georlegacy.general.theatrical.tiles.TileTungstenFixture;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TilePipe extends TileBase implements ITickable, ITheatricalPowerStorage {


    private int ticks = 0;

    private int power = 0;


    @Override
    public void invalidate() {
        if (hasWorld()) {
            WorldPipePanelNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (hasWorld()) {
            WorldPipePanelNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == TheatricalPower.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == TheatricalPower.CAP) {
            return TheatricalPower.CAP.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }
        if (world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null) {
            BlockPos light = pos.offset(EnumFacing.DOWN);
            TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.DOWN));
            if (tileEntity instanceof TileTungstenFixture) {
                TileTungstenFixture tileTungstenFixture = (TileTungstenFixture) tileEntity;
                ITheatricalPowerStorage theatricalPowerStorage = tileTungstenFixture.getCapability(TheatricalPower.CAP, null);
                int drain = theatricalPowerStorage.receiveEnergy(getEnergyStored(), false);
                world.notifyBlockUpdate(light, world.getBlockState(light), world.getBlockState(light), 11);
                extractEnergy(drain, false);
            }
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(255 - power, Math.min(1000, maxReceive));
        if (!simulate) {
            power += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        int energyExtracted = Math.min(power, Math.min(1000, maxExtract));
        if (!simulate) {
            power -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return power;
    }

    @Override
    public int getMaxEnergyStored() {
        return 255;
    }

    @Override
    public boolean canExtract() {
        return power > 0;
    }

    @Override
    public boolean canReceive() {
        return power < 255;
    }
}
