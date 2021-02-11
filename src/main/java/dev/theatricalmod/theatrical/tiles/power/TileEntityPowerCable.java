package dev.theatricalmod.theatrical.tiles.power;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public class TileEntityPowerCable extends TileEntity implements ITickableTileEntity, IEnergyStorage {

    public TileEntityPowerCable() {
        super(TheatricalTiles.POWER_CABLE.get());
    }

    private final int transferRate = 6000;

    private int ticksSinceLastSend = 0;

    private final ArrayList<Direction> sendingFace = new ArrayList<Direction>();

    public boolean isConnected(Direction direction) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(direction));
        if (tileEntity == null) {
            return false;
        }
        if (tileEntity instanceof TileEntityPowerCable) {
            return true;
        }
        if (tileEntity.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).isPresent()) {
            return true;
        }
        if (tileEntity instanceof IAcceptsCable) {
            if (canAcceptPower((IAcceptsCable) tileEntity, direction.getOpposite())) {
                return true;
            }
        }
        return tileEntity.getCapability(DMXReceiver.CAP, direction.getOpposite()).isPresent() || tileEntity.getCapability(
            DMXProvider.CAP, direction.getOpposite()).isPresent();
    }

    public boolean canAcceptPower(IAcceptsCable cable, Direction side){
        return Arrays.stream(cable.getAcceptedCables(side)).anyMatch(cableType -> cableType == CableType.POWER);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            if (side == null || isConnected(side)) {
                return LazyOptional.of(() -> (T) this);
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        ticksSinceLastSend++;
        if (ticksSinceLastSend >= 10) {
            sendingFace.clear();
            ticksSinceLastSend = 0;
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
