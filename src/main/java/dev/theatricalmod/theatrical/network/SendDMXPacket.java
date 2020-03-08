package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SendDMXPacket {

    public SendDMXPacket(BlockPos blockPos, byte[] data) {
        this.blockPos = blockPos;
        this.data = data;
    }

    public SendDMXPacket(PacketBuffer buffer){
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

    private BlockPos blockPos;
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
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleDMXUpdate(blockPos, data));
        context.get().setPacketHandled(true);
    }

}
