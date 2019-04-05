package com.georlegacy.general.theatrical.api.capabilities.dmx.receiver;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDMXReceiver {

    int getChannelCount();

    int getStartPoint();

    void receiveDMXValues(byte[] data, World world, BlockPos pos);

    byte getChannel(int index);

    void updateChannel(int index, byte value);

    void setDMXStartPoint(int dmxStartPoint);

    void setChannelCount(int channelCount);
}
