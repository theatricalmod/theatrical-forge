package dev.theatricalmod.theatrical.api.capabilities.socapex;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class SocapexPatch {

    private BlockPos receiver;
    private int receiverSocket;

    public SocapexPatch() {
    }

    public SocapexPatch(BlockPos pos, int receiverSocket) {
        this.receiver = pos;
        this.receiverSocket = receiverSocket;
    }

    public CompoundTag serialize() {
        CompoundTag nbtTagCompound = new CompoundTag();
        if (receiver != null) {
            nbtTagCompound.putInt("receiver", receiverSocket);
            nbtTagCompound.putInt("x", receiver.getX());
            nbtTagCompound.putInt("y", receiver.getY());
            nbtTagCompound.putInt("z", receiver.getZ());
        }
        return nbtTagCompound;
    }

    public void deserialize(CompoundTag nbtTagCompound) {
        if (nbtTagCompound.contains("x")) {
            this.receiver = new BlockPos(nbtTagCompound.getInt("x"), nbtTagCompound.getInt("y"), nbtTagCompound.getInt("z"));
        }
        this.receiverSocket = nbtTagCompound.getInt("receiver");
    }

    public BlockPos getReceiver() {
        return receiver;
    }

    public int getReceiverSocket() {
        return receiverSocket;
    }
}
