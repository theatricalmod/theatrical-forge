package dev.theatricalmod.theatrical.api.dmx;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IDMXReceiver {

    int getChannelCount();

    int getStartPoint();

    void receiveDMXValues(byte[] data);

    byte getChannel(int index);

    void updateChannel(int index, byte value);

    void setDMXStartPoint(int dmxStartPoint);

    void setChannelCount(int channelCount);
}
