package dev.theatricalmod.theatrical.tiles.power;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public class TileEntityPowerCable extends BlockEntity implements IEnergyStorage {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityPowerCable tile = (TileEntityPowerCable) be;
        if (level.isClientSide) {
            return;
        }
        tile.ticksSinceLastSend++;
        if (tile.ticksSinceLastSend >= 10) {
            tile.sendingFace.clear();
            tile.ticksSinceLastSend = 0;
        }
    }

    public TileEntityPowerCable(BlockPos pos, BlockState state) {
        super(TheatricalTiles.POWER_CABLE.get(), pos, state);
    }

    private final int transferRate = 6000;

    private int ticksSinceLastSend = 0;

    private final ArrayList<Direction> sendingFace = new ArrayList<Direction>();

    public boolean isConnected(Direction direction) {
        BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(direction));
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
        return tileEntity.getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, direction.getOpposite()).isPresent() || tileEntity.getCapability(
            TheatricalCapabilities.CAPABILITY_DMX_PROVIDER, direction.getOpposite()).isPresent();
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
