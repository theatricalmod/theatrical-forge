package com.georlegacy.general.theatrical.api.capabilities.provider;

import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.receiver.IDMXReceiver;
import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import java.util.HashMap;
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
    private HashMap<IDMXReceiver, BlockPos> devices = null;

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


    public void addToList(HashMap<IDMXReceiver, BlockPos> scanned, World world, BlockPos pos, EnumFacing facing){
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity != null && tileEntity.hasCapability(DMXReceiver.CAP, facing)){
            if(!scanned.containsKey(tileEntity.getCapability(DMXReceiver.CAP, facing))){
                scanned.put(tileEntity.getCapability(DMXReceiver.CAP, facing), pos);
                for(EnumFacing facing1 : EnumFacing.VALUES){
                    if(facing1 != facing){
                        addToList(scanned, world, pos.offset(facing1), facing1.getOpposite());
                    }
                }
            }
        }
    }

    @Override
    public void updateDevices(World world, BlockPos controllerPos) {
        if(devices == null) {
            if (world.isRemote) {
                devices = new HashMap<>();
                return;
            }

            HashMap<IDMXReceiver, BlockPos> receivers = new HashMap<>();
            for(EnumFacing facing : EnumFacing.VALUES){
                addToList(receivers, world, controllerPos.offset(facing), facing);
            }
            devices = new HashMap<>(receivers);
        }
        for(IDMXReceiver provider : devices.keySet()) {
            provider.receiveDMXValues(dmxUniverse.getDMXChannels(), world, devices.get(provider));
        }
    }

    @Override
    public void refreshDevices() {
        devices = null;
    }
}
