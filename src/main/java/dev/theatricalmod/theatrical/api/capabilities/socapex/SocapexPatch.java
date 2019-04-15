package dev.theatricalmod.theatrical.api.capabilities.socapex;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class SocapexPatch {

    private BlockPos receiver;
    private int receiverSocket;

    public SocapexPatch() {
    }

    public SocapexPatch(BlockPos pos, int receiverSocket) {
        this.receiver = pos;
        this.receiverSocket = receiverSocket;
    }

    public NBTTagCompound serialize() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        if (receiver != null) {
            nbtTagCompound.setInteger("receiver", receiverSocket);
            nbtTagCompound.setInteger("x", receiver.getX());
            nbtTagCompound.setInteger("y", receiver.getY());
            nbtTagCompound.setInteger("z", receiver.getZ());
        }
        return nbtTagCompound;
    }

    public void deserialize(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.hasKey("x")) {
            this.receiver = new BlockPos(nbtTagCompound.getInteger("x"), nbtTagCompound.getInteger("y"), nbtTagCompound.getInteger("z"));
        }
        this.receiverSocket = nbtTagCompound.getInteger("receiver");
    }

    public BlockPos getReceiver() {
        return receiver;
    }

    public int getReceiverSocket() {
        return receiverSocket;
    }
}
