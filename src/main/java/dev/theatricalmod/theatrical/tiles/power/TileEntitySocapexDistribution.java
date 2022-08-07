package dev.theatricalmod.theatrical.tiles.power;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TileEntitySocapexDistribution extends BlockEntity implements IAcceptsCable, ISocapexReceiver {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntitySocapexDistribution tile = (TileEntitySocapexDistribution) be;
        if(!level.isClientSide) {
            for (Direction direction : Direction.values()) {
                if (direction == tile.getFacing()) {
                    continue;
                }
                BlockEntity tileEntity = level.getBlockEntity(pos.relative(direction));
                if (tileEntity != null) {
                    LazyOptional<ITheatricalPowerStorage> capability = tileEntity.getCapability(TheatricalPower.CAP, direction.getOpposite());
                    capability.ifPresent(iTheatricalPowerStorage -> {
                        if (iTheatricalPowerStorage.getEnergyStored() > tile.getEnergyStored(tile.getDirectionalIndex(direction))) {
                            return;
                        }
                        if (iTheatricalPowerStorage.receiveEnergy(255, true) > 0) {
                            int amount = iTheatricalPowerStorage.receiveEnergy(tile.getEnergyStored(tile.getDirectionalIndex(direction)), false);
                            tile.channels[tile.getDirectionalIndex(direction)] = tile.channels[tile.getDirectionalIndex(direction)] - amount;
                        }
                    });
                }
            }
        }
    }

    private final int[] channels;
    private final Direction[] mappedDirections = new Direction[5];

    public TileEntitySocapexDistribution(BlockPos blockPos, BlockState blockState) {
        super(TheatricalTiles.SOCAPEX_DISTRIBUTION.get(), blockPos, blockState);
        channels = new int[6];
    }

    public Direction getFacing(){
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    public int getDirectionalIndex(Direction direction) {
        for(int i = 0; i < mappedDirections.length; i++){
            if(mappedDirections[i] == direction){
                return i;
            }
        }
        return -1;
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
                this.channels[i] += energyReceived[i];
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
        if(channel == getDirectionalIndex(getFacing())){
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
    public void setLevel(Level p_155231_) {
        super.setLevel(p_155231_);
        int i = 0;
        for (Direction direction : Direction.values()) {
            if(direction == getFacing()){
                continue;
            }
            mappedDirections[i] = direction;
            i++;
        }
    }

    @Override
    public BlockPos getReceiverPos() {
        return super.getBlockPos();
    }
}
