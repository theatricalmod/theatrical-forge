package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.dmx.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.capability.CapabilityDMXProvider;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.Random;

public class TileEntityTestDMX extends BlockEntity {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityTestDMX tile = (TileEntityTestDMX) be;
        if (level.isClientSide) {
            return;
        }
        tile.ticks++;
        if (tile.ticks >= 20) {
            byte[] data = tile.generateRandomDMX();
            tile.idmxProvider.getUniverse(level).setDmxChannels(data);
            TheatricalNetworkHandler.MAIN.send(PacketDistributor.DIMENSION.with(level::dimension), new SendDMXProviderPacket(pos, data));
            tile.sendDMXSignal();
            tile.ticks = 0;
        }
    }

    private final IDMXProvider idmxProvider;

    private int ticks = 0;

    public TileEntityTestDMX(BlockPos pos, BlockState state) {
        super(TheatricalTiles.TEST_DMX.get(), pos, state);
        this.idmxProvider = new CapabilityDMXProvider(new DMXUniverse());
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
        if (cap == TheatricalCapabilities.CAPABILITY_DMX_PROVIDER) {
            return TheatricalCapabilities.CAPABILITY_DMX_PROVIDER.orEmpty(cap, LazyOptional.of(() -> idmxProvider));
        }
        return super.getCapability(cap, direction);
    }

    public void sendDMXSignal() {
        idmxProvider.updateDevices(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        if (hasLevel()) {
//            level.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
        super.setRemoved();
    }

    @Override
    public void setLevel(Level p_155231_) {
        super.setLevel(p_155231_);
        if (hasLevel()) {
//            level.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
    }
}
