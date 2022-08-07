package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeDimmerPatchPacket {

    public ChangeDimmerPatchPacket(BlockPos blockPos, int channel, int socketNumber, SocapexPatch patch) {
        this.blockPos = blockPos;
        this.channel = channel;
        this.socketNumber = socketNumber;
        this.patch = patch;
    }

    public ChangeDimmerPatchPacket(FriendlyByteBuf buffer) {
        channel = buffer.readInt();
        patch = new SocapexPatch();
        patch.deserialize(
            EntityDataSerializers.COMPOUND_TAG.read(buffer));
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        blockPos = new BlockPos(x, y, z);
        socketNumber = buffer.readInt();
    }

    private final BlockPos blockPos;
    private final int channel;
    private final int socketNumber;
    private final SocapexPatch patch;

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(channel);
        EntityDataSerializers.COMPOUND_TAG.write(buf, patch.serialize());
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(socketNumber);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleChangeDimmerPatch(context.get(), blockPos, patch, channel, socketNumber));
        context.get().setPacketHandled(true);
    }

}
