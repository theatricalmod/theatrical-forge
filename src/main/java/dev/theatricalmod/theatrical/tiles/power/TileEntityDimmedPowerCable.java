package dev.theatricalmod.theatrical.tiles.power;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public class TileEntityDimmedPowerCable extends BlockEntity implements ITheatricalPowerStorage {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityDimmedPowerCable tile = (TileEntityDimmedPowerCable) be;
        if (level.isClientSide) {
            return;
        }
        tile.ticksSinceLastSend++;
        if (tile.ticksSinceLastSend >= 10) {
            tile.sendingFace.clear();
            tile.ticksSinceLastSend = 0;
        }

        tile.doEnergyTransfer();
    }
    public TileEntityDimmedPowerCable(BlockPos pos, BlockState state) {
        super(TheatricalTiles.DIMMED_POWER_CABLE.get(), pos, state);
    }

    public int power = 0;
    private final int transferRate = 6000;

    private int ticksSinceLastSend = 0;

    private final ArrayList<Direction> sendingFace = new ArrayList<Direction>();


    public CompoundTag writeNBT(CompoundTag nbtTagCompound) {
        return nbtTagCompound;
    }

    public void readNBT(CompoundTag nbt) {
        if (nbt.contains("power")) {
            power = nbt.getInt("power");
        }
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        readNBT(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(writeNBT(tag));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        readNBT(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        readNBT(tag);
    }

    public boolean isConnected(Direction direction) {
        BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(direction));
        if (tileEntity == null) {
            return false;
        }
        if (tileEntity instanceof TileEntityDimmedPowerCable) {
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
        //TODO: don't use this, it's not a DMX cable?!
        return tileEntity.getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, direction.getOpposite()).isPresent() || tileEntity.getCapability(
            TheatricalCapabilities.CAPABILITY_DMX_PROVIDER, direction.getOpposite()).isPresent();
    }

    public boolean canAcceptPower(IAcceptsCable cable, Direction side){
        return Arrays.stream(cable.getAcceptedCables(side)).anyMatch(cableType -> cableType == CableType.DIMMED_POWER);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == TheatricalPower.CAP) {
            if (side == null) {
                return LazyOptional.of(() -> (T) this);
            }
            if (isConnected(side)) {
                return LazyOptional.of(() -> (T) this);
            }
        }
        return super.getCapability(cap, side);
    }


    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(transferRate, maxReceive));
        if (!simulate) {
            power += energyReceived;
        }
//        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        int energyExtracted = Math.min(getEnergyStored(), Math.min(transferRate, maxExtract));
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
        return getEnergyStored() != 0;
    }

    @Override
    public boolean canReceive() {
        return getMaxEnergyStored() != getEnergyStored();
    }

    public boolean canReceiveFromFace(Direction facing) {
        if (sendingFace.contains(facing)) {
            return false;
        }
        return canReceive();
    }

    public void doEnergyTransfer() {
        if (!canExtract()) {
            return;
        }
        ArrayList<ITheatricalPowerStorage> acceptors = new ArrayList<>();
        for (Direction face : Direction.values()) {
            BlockPos newPos = worldPosition.relative(face);
            BlockEntity tile = level.getBlockEntity(newPos);
            if (tile == null) {
                continue;
            }else if (tile instanceof TileEntityDimmedPowerCable) {
                TileEntityDimmedPowerCable cable = (TileEntityDimmedPowerCable) tile;
                if (power > cable.power && cable.canReceiveFromFace(face.getOpposite())) {
                    acceptors.add((ITheatricalPowerStorage) tile);
                    if (!sendingFace.contains(face)) {
                        sendingFace.add(face);
                    }
                }
            } else if (tile.getCapability(TheatricalPower.CAP, face.getOpposite()).isPresent()) {
                tile.getCapability(TheatricalPower.CAP, face.getOpposite()).ifPresent(iTheatricalPowerStorage -> {
                    if(iTheatricalPowerStorage.canReceive()){
                        acceptors.add(iTheatricalPowerStorage);
                    }
                });
            }
        }
        if (acceptors.size() > 0) {
            for (ITheatricalPowerStorage tile : acceptors) {
                int drain = Math.min(power, transferRate);
                if (drain > 0 && tile.receiveEnergy(drain, true) > 0) {
                    int move = tile.receiveEnergy(drain, false);
                    extractEnergy(move, false);
                }
            }
        }
    }

}
