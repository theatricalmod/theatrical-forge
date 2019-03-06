package com.georlegacy.general.theatrical.api.capabilities.receiver;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDMXReceiver {

    int getChannelCount();

    int getStartPoint();

    void receiveDMXValues(int[] data, EnumFacing facing, World world, BlockPos pos);

    int getChannel(int index);

    void updateChannel(int index, int value);

    void setDMXStartPoint(int dmxStartPoint);

    void setChannelCount(int channelCount);
}
