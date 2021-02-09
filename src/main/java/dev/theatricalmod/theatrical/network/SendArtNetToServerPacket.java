package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SendArtNetToServerPacket {

    public SendArtNetToServerPacket(BlockPos blockPos, byte[] data) {
        this.blockPos = blockPos;
        this.data = data;
    }

    public SendArtNetToServerPacket(PacketBuffer buffer){
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        blockPos = new BlockPos(x, y, z);
        int i = buffer.readInt();
        if(data == null){
            data = new byte[i];
        }
        buffer.readBytes(data);
    }

    private World world;
    private final BlockPos blockPos;
    private byte[] data;

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public byte[] getData() {
        return data;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public void handle(Supplier<Context> context){
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleArtNetPacket(context.get(), blockPos, data));
        context.get().setPacketHandled(true);
    }

}
