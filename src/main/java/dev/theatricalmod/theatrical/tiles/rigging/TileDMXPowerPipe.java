package dev.theatricalmod.theatrical.tiles.rigging;

import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.tiles.TileBase;
import dev.theatricalmod.theatrical.tiles.TileDMXAcceptor;
import dev.theatricalmod.theatrical.tiles.cables.CableType;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class TileDMXPowerPipe extends TileBase implements ITickable, IAcceptsCable {


    private int ticks = 0;

    private DMXReceiver receiver;
    private EnergyStorage energyStorage;


    private ArrayList<EnumFacing> sendingFace = new ArrayList<EnumFacing>();

    public TileDMXPowerPipe() {
        receiver = new DMXReceiver(255, 0);
        energyStorage = new EnergyStorage(5, 1000);
    }

    @Override
    public void invalidate() {
        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return true;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return DMXReceiver.CAP.cast(receiver);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }

    public boolean canReceiveFromFace(EnumFacing facing) {
        if (sendingFace.contains(facing)) {
            return false;
        }
        return energyStorage.canReceive();
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }
        if (world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null) {
            BlockPos light = pos.offset(EnumFacing.DOWN);
            TileEntity tileEntity = world.getTileEntity(light);
            if (tileEntity instanceof TileDMXAcceptor) {
                TileDMXAcceptor tileDMXAcceptor = (TileDMXAcceptor) tileEntity;
                tileDMXAcceptor.getCapability(DMXReceiver.CAP, null).receiveDMXValues(receiver.getDmxValues(), world, light);
            }
        }
        ticks++;
        if (ticks > 10) {
            sendingFace.clear();
            ticks = 0;
        }
        if (!energyStorage.canExtract()) {
            return;
        }
        ArrayList<IEnergyStorage> acceptors = new ArrayList<>();
        for (EnumFacing face : EnumFacing.VALUES) {
            BlockPos newPos = pos.offset(face);
            TileEntity tile = world.getTileEntity(newPos);
            if (tile == null) {
                continue;
            } else if (tile instanceof TileDMXPowerPipe) {
                TileDMXPowerPipe powerPipe = (TileDMXPowerPipe) tile;
                if (energyStorage.getEnergyStored() > powerPipe.energyStorage.getEnergyStored() && powerPipe.canReceiveFromFace(face.getOpposite())) {
                    acceptors.add(tile.getCapability(CapabilityEnergy.ENERGY, null));
                    if (!sendingFace.contains(face)) {
                        sendingFace.add(face);
                    }
                }
            } else if (tile.getCapability(CapabilityEnergy.ENERGY, face.getOpposite()) != null) {
                IEnergyStorage energyTile = tile.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
                if (energyTile != null && energyTile.canReceive()) {
                    acceptors.add(energyTile);
                }
            }
        }
        if (acceptors.size() > 0) {
            for (IEnergyStorage tile : acceptors) {
                int drain = Math.min(energyStorage.getEnergyStored(), 1000);
                if (drain > 0 && tile.receiveEnergy(drain, true) > 0) {
                    int move = tile.receiveEnergy(drain, false);
                    energyStorage.extractEnergy(move, false);
                }
            }
        }
    }

    @Override
    public CableType[] getAcceptedCables() {
        return new CableType[]{CableType.DMX};
    }
}
