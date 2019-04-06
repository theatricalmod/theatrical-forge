package com.georlegacy.general.theatrical.api.capabilities.socapex;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISocapexProvider {

    void updateDevices(World world, BlockPos controllerPos);

    void refreshDevices();

    int[] receiveSocapex(int[] channels, boolean simulate);

    int[] extractSocapex(int[] channels, boolean simulate);

    boolean canReceive(int channel);

    boolean canExtract(int channel);

    String getPatch(int channel);

    void patch(int dmxChannel, ISocapexReceiver receiver, int socket);

    boolean hasPatch(ISocapexReceiver receiver);

    int[] getChannelsForReceiver(ISocapexReceiver receiver);

    List<ISocapexReceiver> getDevices(World world, BlockPos controller);
}
