package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

public class TileEntityTestDMX extends TileEntity implements ITickableTileEntity {

    private final IDMXProvider idmxProvider;

    private int ticks = 0;

    public TileEntityTestDMX() {
        super(TheatricalTiles.TEST_DMX.get());
        this.idmxProvider = new DMXProvider(new DMXUniverse());
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        ticks++;
        if (ticks >= 20) {
            byte[] data = generateRandomDMX();
            this.idmxProvider.getUniverse(world).setDmxChannels(data);
            Dimension dimension = world.dimension;
            TheatricalNetworkHandler.MAIN.send(PacketDistributor.DIMENSION.with(dimension::getType), new SendDMXProviderPacket(pos, data));
            sendDMXSignal();
            ticks = 0;
        }
    }

    public byte[] generateRandomDMX() {
        byte[] dmx = new byte[512];
        Random random = new Random();
        random.nextBytes(dmx);
        return dmx;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction direction) {
        if (cap == DMXProvider.CAP) {
            return DMXProvider.CAP.orEmpty(cap, LazyOptional.of(() -> idmxProvider));
        }
        return super.getCapability(cap, direction);
    }

    public void sendDMXSignal() {
        idmxProvider.updateDevices(world, pos);
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
}
