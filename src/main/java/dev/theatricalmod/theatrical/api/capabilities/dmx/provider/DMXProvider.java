package dev.theatricalmod.theatrical.api.capabilities.dmx.provider;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.block.cables.CableBlockEntity;
import java.util.HashSet;
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


    public void addToList(HashSet<BlockPos> scanned, World world, BlockPos pos, Direction facing){
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity != null && (tileEntity.getCapability(DMXReceiver.CAP, facing).isPresent() || tileEntity instanceof CableBlockEntity)){
            if (scanned.add(pos)) {
                if(tileEntity instanceof CableBlockEntity){
                    CableBlockEntity cable = (CableBlockEntity) tileEntity;
                    for(Direction direction : Direction.values()){
                        if(cable.hasType(CableType.DMX) && cable.isConnected(direction, CableType.DMX)){
                            addToList(scanned, world, cable.getPos().offset(direction), direction.getOpposite());
                        }
                    }
                }else{
                    for (Direction facing1 : Direction.values()) {
                        if (facing1 != facing) {
                            addToList(scanned, world, pos.offset(facing1), facing1.getOpposite());
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
            for(Direction facing : Direction.values()){
                addToList(receivers, world, controllerPos.offset(facing), facing.getOpposite());
            }
            devices = new HashSet<>(receivers);
        }
        for (BlockPos receiver : devices) {
            TileEntity tile = world.getTileEntity(receiver);
            if(tile != null && !(tile instanceof CableBlockEntity)) {
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
