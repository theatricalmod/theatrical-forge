package dev.theatricalmod.theatrical.tiles.lights;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.dmx.IDMXReceiver;
import dev.theatricalmod.theatrical.capability.CapabilityDMXReceiver;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityFixtureDMXAcceptor extends TileEntityFixture implements IAcceptsCable {

    private final IDMXReceiver idmxReceiver;

    public TileEntityFixtureDMXAcceptor(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.idmxReceiver = new CapabilityDMXReceiver(0, 0);
    }

    @Override
    public CompoundTag getNBT(@Nullable CompoundTag compoundNBT) {
        CompoundTag tag = super.getNBT(compoundNBT);
        tag.putInt("channelCount", idmxReceiver.getChannelCount());
        tag.putInt("channelStartPoint", idmxReceiver.getStartPoint());
        return tag;
    }

    @Override
    public void readNBT(CompoundTag compoundNBT) {
        super.readNBT(compoundNBT);
        idmxReceiver.setChannelCount(compoundNBT.getInt("channelCount"));
        idmxReceiver.setDMXStartPoint(compoundNBT.getInt("channelStartPoint"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == TheatricalCapabilities.CAPABILITY_DMX_PROVIDER) {
            return LazyOptional.of(() -> (T) idmxReceiver);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        if (hasLevel()) {
            //TODO: DMX network
//            level.getCapability(WorldDMXNetwork).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
        super.setRemoved();
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.DMX, CableType.DIMMED_POWER};
    }

    public IDMXReceiver getIdmxReceiver() {
        return idmxReceiver;
    }
}
