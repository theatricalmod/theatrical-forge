package dev.theatricalmod.theatrical.api.capabilities.dmx.provider;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.block.cables.BlockCable;
import java.util.HashSet;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class DMXProvider implements IDMXProvider, INBTSerializable<CompoundNBT> {

    @CapabilityInject(IDMXProvider.class)
    public static Capability<IDMXProvider> CAP = null;

    private DMXUniverse dmxUniverse;
    private HashSet<BlockPos> devices = null;

    public DMXProvider() {}

    public DMXProvider(DMXUniverse dmxUniverse){
        this.dmxUniverse = dmxUniverse;
    }

    @Override
    public byte[] sendDMXValues(DMXUniverse dmxUniverse) {
        return dmxUniverse.getDMXChannels();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    @Override
    public DMXUniverse getUniverse(World world) {
        return dmxUniverse;
    }


    public void addToList(HashSet<BlockPos> scanned, World world, BlockPos pos, Direction facing, HashSet<BlockPos> scannedCable){
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockCable && ((BlockCable) blockState.getBlock()).getCableType() == CableType.DMX) {
            scannedCable.add(pos);
            for (Direction direction : Direction.values()) {
                if(direction != facing) {
                    if (((BlockCable) blockState.getBlock()).canConnect(world, pos, direction) && !scannedCable.contains(pos.offset(direction))) {
                        addToList(scanned, world, pos.offset(direction), direction.getOpposite(), scannedCable);
                    }
                }
            }
        } else {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null && (tileEntity.getCapability(DMXReceiver.CAP, facing).isPresent())) {
                if (scanned.add(pos)) {
                    for (Direction facing1 : Direction.values()) {
                        if (facing1 != facing) {
                            addToList(scanned, world, pos.offset(facing1), facing1.getOpposite(), scannedCable);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateDevices(World world, BlockPos controllerPos) {
        if(devices == null) {
//            if (world.isRemote) {
//                devices = new HashSet<>();
//                return;
//            }
            HashSet<BlockPos> receivers = new HashSet<>();
            HashSet<BlockPos> scannedCable = new HashSet<>();
            for(Direction facing : Direction.values()){
                addToList(receivers, world, controllerPos.offset(facing), facing.getOpposite(), scannedCable);
            }
            scannedCable.clear();
            devices = new HashSet<>(receivers);
        }
        for (BlockPos receiver : devices) {
            BlockState blockState = world.getBlockState(receiver);
            if (blockState.getBlock() instanceof BlockCable) {
                continue;
            }
            TileEntity tile = world.getTileEntity(receiver);
            if (tile != null) {
                IDMXReceiver idmxReceiver = tile.getCapability(DMXReceiver.CAP, null).orElse(null);
                if (idmxReceiver != null) {
                    idmxReceiver.receiveDMXValues(dmxUniverse.getDMXChannels(), world, receiver);
                }
            }
        }
    }

    @Override
    public void refreshDevices() {
        devices = null;
    }
}
