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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CapabilityDMXReceiver implements IDMXReceiver {

    private int dmxChannels;
    private int dmxStartPoint;
    private byte[] dmxBuffer;

    public CapabilityDMXReceiver(int dmxChannels, int dmxStartPoint){
        this.dmxChannels = dmxChannels;
        this.dmxStartPoint = dmxStartPoint;
        this.dmxBuffer = new byte[dmxChannels];
    }


    @Override
    public int getChannelCount() {
        return dmxChannels;
    }

    @Override
    public int getStartPoint() {
        return dmxStartPoint;
    }

    @Override
    public void receiveDMXValues(byte[] data) {
        if(data.length > this.dmxStartPoint) {
            this.dmxBuffer = Arrays.copyOfRange(data, this.dmxStartPoint, this.dmxStartPoint + this.dmxChannels);
        }
    }

    @Override
    public byte getChannel(int index) {
        return this.dmxBuffer[index];
    }

    @Override
    public void updateChannel(int index, byte value) {
        this.dmxBuffer[index] = value;
    }

    @Override
    public void setDMXStartPoint(int dmxStartPoint) {
        this.dmxStartPoint = dmxStartPoint;
    }

    @Override
    public void setChannelCount(int channelCount) {
        this.dmxChannels = channelCount;
    }
}
