package com.georlegacy.general.theatrical.api.capabilities.receiver;

import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.packets.SendDMXPacket;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class DMXReceiver implements IDMXReceiver, INBTSerializable<NBTTagCompound> {

    @CapabilityInject(IDMXReceiver.class)
    public static Capability<IDMXReceiver> CAP;

    private int dmxStartPoint;
    private int dmxChannels;
    private int[] dmxValues;
    private EnumFacing lastSignalFrom;

    public DMXReceiver(int dmxChannels, int dmxStartPoint){
        this.dmxChannels = dmxChannels;
        this.dmxStartPoint = dmxStartPoint;
        this.dmxValues = new int[dmxChannels];
    }

    @Override
    public int getChannelCount() {
        return dmxChannels;
    }

    @Override
    public int getStartPoint() {
        return dmxStartPoint;
    }

    public int getDMXChannel(int channel){
        return dmxValues[channel];
    }

    @Override
    public void receiveDMXValues(int[] data, EnumFacing facing, World world, BlockPos pos) {
        lastSignalFrom = facing;
        if(data.length > this.dmxStartPoint) {
            this.dmxValues = Arrays.copyOfRange(data, this.dmxStartPoint, this.dmxStartPoint + this.dmxChannels);
        }
        if(!world.isRemote)
            TheatricalPacketHandler.INSTANCE.sendToAll(new SendDMXPacket(pos, dmxValues));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    public void setDMXStartPoint(int dmxStartPoint) {
        this.dmxStartPoint = dmxStartPoint;
    }

    @Override
    public void setChannelCount(int channelCount) {
        this.dmxChannels = channelCount;
    }

    @Override
    public int getChannel(int index) {
        if(dmxValues.length < (index + 1) || dmxValues.length == 0){
            return 0;
        }
        return dmxValues[index];
    }

    @Override
    public void updateChannel(int index, int value) {
        this.dmxValues[index] = value;
    }

    public EnumFacing getLastSignalFrom() {
        return lastSignalFrom;
    }


}
