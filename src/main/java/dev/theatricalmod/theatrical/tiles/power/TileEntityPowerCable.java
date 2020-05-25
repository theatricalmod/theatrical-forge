package dev.theatricalmod.theatrical.tiles.power;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityPowerCable extends TileEntity implements ITheatricalPowerStorage, ITickableTileEntity {

    public TileEntityPowerCable() {
        super(TheatricalTiles.POWER_CABLE.get());
    }

    public int power = 0;
    private int transferRate = 6000;

    private int ticksSinceLastSend = 0;

    private ArrayList<Direction> sendingFace = new ArrayList<Direction>();


    public CompoundNBT writeNBT(CompoundNBT nbtTagCompound) {
        return nbtTagCompound;
    }

    public void readNBT(CompoundNBT nbt) {
        if (nbt.contains("power")) {
            power = nbt.getInt("power");
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        readNBT(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return super.write(writeNBT(compound));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getNbtCompound();
        readNBT(tag);
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        readNBT(tag);
    }

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
            BlockPos newPos = pos.offset(face);
            TileEntity tile = world.getTileEntity(newPos);
            if (tile == null) {
                continue;
            }else if (tile instanceof TileEntityPowerCable) {
                TileEntityPowerCable cable = (TileEntityPowerCable) tile;
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

        doEnergyTransfer();
    }

}
