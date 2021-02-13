package dev.theatricalmod.theatrical.tiles.power;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TileEntitySocapexDistribution extends TileEntity implements IAcceptsCable, ISocapexReceiver, ITickableTileEntity {

    private final int[] channels;

    public TileEntitySocapexDistribution() {
        super(TheatricalTiles.SOCAPEX_DISTRIBUTION.get());
        channels = new int[6];
    }

    public Direction getFacing(){
        return getBlockState().get(BlockStateProperties.FACING);
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        if(side == getFacing()){
            return new CableType[]{CableType.SOCAPEX};
        } else {
            return new CableType[]{CableType.DIMMED_POWER};
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == SocapexReceiver.CAP){
            if(side == getFacing()){
                return LazyOptional.of(() -> (T) this);
            } else {
                return LazyOptional.empty();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public int[] receiveSocapex(int[] channels, boolean simulate) {
        int[] energyReceived = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canReceive(i)) {
                energyReceived[i] = 0;
                continue;
            }
            energyReceived[i] = Math.min(255 - this.channels[i], Math.min(1000, channels[i]));
            if (!simulate) {
                this.channels[i] = energyReceived[i];
            }
        }
        return energyReceived;
    }

    @Override
    public int[] extractSocapex(int[] channels, boolean simulate) {
        return new int[6];
    }

    @Override
    public int getEnergyStored(int channel) {
        return channels[channel];
    }

    @Override
    public int getMaxEnergyStored(int channel) {
        return 255;
    }

    @Override
    public boolean canExtract(int channel) {
        return false;
    }

    @Override
    public boolean canReceive(int channel) {
        if(channel == getFacing().getIndex()){
            return false;
        }
        return channel <= this.channels.length - 1;
    }

    @Override
    public List<BlockPos> getDevices() {
        return Collections.emptyList();
    }

    @Override
    public int getTotalChannels() {
        return 5;
    }

    @Override
    public void tick() {
        if(!world.isRemote) {
            for (Direction direction : Direction.values()) {
                if (direction == getFacing()) {
                    continue;
                }
                TileEntity tileEntity = world.getTileEntity(pos.offset(direction));
                if (tileEntity != null) {
                    LazyOptional<ITheatricalPowerStorage> capability = tileEntity.getCapability(TheatricalPower.CAP, direction.getOpposite());
                    capability.ifPresent(iTheatricalPowerStorage -> {
                        if (iTheatricalPowerStorage.getEnergyStored() > getEnergyStored(direction.getIndex())) {
                            return;
                        }
                        if (iTheatricalPowerStorage.receiveEnergy(255, true) > 0) {
                            int amount = iTheatricalPowerStorage.receiveEnergy(getEnergyStored(direction.getIndex()), false);
                            channels[direction.getIndex()] = channels[direction.getIndex()] - amount;
                        }
                    });
                }
            }
        }
    }

    @Override
    public BlockPos getReceiverPos() {
        return super.getPos();
    }
}
