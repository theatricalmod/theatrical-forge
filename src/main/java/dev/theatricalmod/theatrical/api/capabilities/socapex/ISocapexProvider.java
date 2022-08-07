package dev.theatricalmod.theatrical.api.capabilities.socapex;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public interface ISocapexProvider {

    void updateDevices(Level world, BlockPos controllerPos);

    void refreshDevices();

    int[] receiveSocapex(int[] channels, boolean simulate);

    int[] extractSocapex(int[] channels, boolean simulate);

    boolean canReceive(int channel);

    boolean canExtract(int channel);

    SocapexPatch[] getPatch(int channel);

    void patch(int dmxChannel, ISocapexReceiver receiver, int receiverSocket, int patchSocket);

    void removePatch(int dmxChannel, int patchSocket);

    boolean hasPatch(ISocapexReceiver receiver);

    int[] getChannelsForReceiver(ISocapexReceiver receiver);

    List<ISocapexReceiver> getDevices(Level world, BlockPos controller);

    int[] getPatchedCables(ISocapexReceiver socapexReceiver);

    String getIdentifier(BlockPos pos);
}
