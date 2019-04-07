package com.georlegacy.general.theatrical.api.capabilities.dmx.provider;

import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import com.georlegacy.general.theatrical.tiles.cables.TileCable;
import java.util.HashSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class DMXProvider implements IDMXProvider, INBTSerializable<NBTTagCompound> {

    @CapabilityInject(IDMXProvider.class)
    public static Capability<IDMXProvider> CAP;

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
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    @Override
    public DMXUniverse getUniverse(World world) {
        return dmxUniverse;
    }


    public void addToList(HashSet<BlockPos> scanned, World world, BlockPos pos, EnumFacing facing){
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity != null && tileEntity.hasCapability(DMXReceiver.CAP, facing)){
            if (scanned.add(pos)) {
                if(tileEntity instanceof TileCable){
                    TileCable cable = (TileCable) tileEntity;
                    for (int i = 0; i < 6; i++) {
                        if (cable.hasSide(i)) {
                            for (EnumFacing facing1 : EnumFacing.VALUES) {
                                if (facing1 != facing) {
                                    BlockPos connected = cable.isConnectedSides(facing1, i, CableType.DMX);
                                    if (connected != null) {
                                        addToList(scanned, world, connected, facing1.getOpposite());
                                    }
                                }
                            }
                        }
                    }
                }else{
                    for (EnumFacing facing1 : EnumFacing.VALUES) {
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
            if (world.isRemote) {
                devices = new HashSet<>();
                return;
            }
            HashSet<BlockPos> receivers = new HashSet<>();
            for(EnumFacing facing : EnumFacing.VALUES){
                addToList(receivers, world, controllerPos.offset(facing), facing.getOpposite());
            }
            devices = new HashSet<>(receivers);
        }
        for (BlockPos receiver : devices) {
            TileEntity tile = world.getTileEntity(receiver);
            if(tile != null && !(tile instanceof TileCable)) {
                IDMXReceiver idmxReceiver = tile.getCapability(DMXReceiver.CAP, null);
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
