package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ChangeDimmerPatchPacket {

    public ChangeDimmerPatchPacket(BlockPos blockPos, int channel, int socketNumber, SocapexPatch patch) {
        this.blockPos = blockPos;
        this.channel = channel;
        this.socketNumber = socketNumber;
        this.patch = patch;
    }

    public ChangeDimmerPatchPacket(PacketBuffer buffer) {
        channel = buffer.readInt();
        patch = new SocapexPatch();
        patch.deserialize(
            DataSerializers.COMPOUND_NBT.read(buffer));
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

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(channel);
        DataSerializers.COMPOUND_NBT.write(buf, patch.serialize());
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(socketNumber);
    }

    public void handle(Supplier<Context> context) {
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleChangeDimmerPatch(context.get(), blockPos, patch, channel, socketNumber));
        context.get().setPacketHandled(true);
    }

}
