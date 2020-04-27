package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class UpdateArtNetInterfacePacket {

    public UpdateArtNetInterfacePacket(BlockPos blockPos, int universe, String ip) {
        this.blockPos = blockPos;
        this.universe = universe;
        this.ipAddress = ip;
    }

    public UpdateArtNetInterfacePacket(PacketBuffer buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        blockPos = new BlockPos(x, y, z);
        universe = buffer.readInt();
        ipAddress = buffer.readString();
    }

    private BlockPos blockPos;
    private int universe;
    private String ipAddress;

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getUniverse() {
        return universe;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(universe);
        buf.writeString(ipAddress);
    }

    public void handle(Supplier<Context> context){
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleUpdateArtNetInterface(context.get(), blockPos, universe, ipAddress));
        context.get().setPacketHandled(true);
    }

}
