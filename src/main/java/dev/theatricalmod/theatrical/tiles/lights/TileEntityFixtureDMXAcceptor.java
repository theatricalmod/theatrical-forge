package dev.theatricalmod.theatrical.tiles.lights;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityFixtureDMXAcceptor extends TileEntityFixture implements IAcceptsCable {

    private final IDMXReceiver idmxReceiver;

    public TileEntityFixtureDMXAcceptor(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.idmxReceiver = new DMXReceiver(0, 0);
    }

    @Override
    public CompoundNBT getNBT(@Nullable CompoundNBT compoundNBT) {
        CompoundNBT tag = super.getNBT(compoundNBT);
        tag.putInt("channelCount", idmxReceiver.getChannelCount());
        tag.putInt("channelStartPoint", idmxReceiver.getStartPoint());
        return tag;
    }

    @Override
    public void readNBT(CompoundNBT compoundNBT) {
        super.readNBT(compoundNBT);
        idmxReceiver.setChannelCount(compoundNBT.getInt("channelCount"));
        idmxReceiver.setDMXStartPoint(compoundNBT.getInt("channelStartPoint"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == DMXReceiver.CAP) {
            return LazyOptional.of(() -> (T) idmxReceiver);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void remove() {
        if (hasWorld()) {
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
        super.remove();
    }

    @Override
    public void setWorldAndPos(World p_226984_1_, BlockPos p_226984_2_) {
        super.setWorldAndPos(p_226984_1_, p_226984_2_);
        if (hasWorld()) {
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.DMX, CableType.DIMMED_POWER};
    }

    public IDMXReceiver getIdmxReceiver() {
        return idmxReceiver;
    }
}
