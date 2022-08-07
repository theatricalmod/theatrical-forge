package dev.theatricalmod.theatrical.capability;

import dev.theatricalmod.theatrical.api.ConnectableType;
import dev.theatricalmod.theatrical.api.IConnectable;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.api.dmx.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.IDMXReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashSet;
import java.util.Set;

public class CapabilityDMXProvider implements IDMXProvider {

    private DMXUniverse dmxUniverse;
    private Set<BlockPos> devices;

    public CapabilityDMXProvider(DMXUniverse dmxUniverse) {
        this.dmxUniverse = dmxUniverse;
    }

    @Override
    public byte[] sendDMXValues(DMXUniverse dmxUniverse) {
        return dmxUniverse.getDMXChannels();
    }

    @Override
    public DMXUniverse getUniverse(Level world) {
        return dmxUniverse;
    }

    @Override
    public void updateDevices(Level world, BlockPos controllerPos) {
        if(devices == null){
            HashSet<BlockPos> scanned = new HashSet<>();
            addToList(scanned, world, controllerPos, null);
            devices = new HashSet<>(scanned);
        }

        for(BlockPos blockPos : devices)
        {
            BlockEntity be = world.getBlockEntity(blockPos);
            IDMXReceiver receiver = be.getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER).orElse(null);
            if(receiver == null){
                continue;
            }
            receiver.receiveDMXValues(dmxUniverse.getDMXChannels());
        }
    }

    public void addToList(Set<BlockPos> scanned, Level world, BlockPos pos, Direction facing) {
        for(Direction d : Direction.values()){
            if(d.getOpposite() == facing){
                continue;
            }
            BlockPos lookPos = pos.relative(d);
            ChunkAccess chunkAccess = world.getChunk(lookPos);
            if(chunkAccess == null){
                continue;
            }
            BlockEntity blockEntity = world.getBlockEntity(lookPos);
            if(blockEntity == null){
                continue;
            }
            LazyOptional<IConnectable> connectableOpt = blockEntity.getCapability(TheatricalCapabilities.CAPABILITY_CONNECTABLE, d.getOpposite());
            if(!connectableOpt.isPresent()){
                continue;
            }
            IConnectable connectable = connectableOpt.orElse(null);
            if(connectable.getPos() == null){
                connectable.setPos(lookPos);
            }
            if(connectable.acceptsType(ConnectableType.DMX)) {
                scanned.add(lookPos);
                addToList(scanned, world, pos, d);
            }
        }
    }

        @Override
    public void refreshDevices() {
        devices = null;
    }
}
