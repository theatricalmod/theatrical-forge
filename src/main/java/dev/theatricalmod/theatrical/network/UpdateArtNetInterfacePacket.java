package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateArtNetInterfacePacket {

    public UpdateArtNetInterfacePacket(BlockPos blockPos, int universe, String ip) {
        this.blockPos = blockPos;
        this.universe = universe;
        this.ipAddress = ip;
    }

    public UpdateArtNetInterfacePacket(FriendlyByteBuf buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        blockPos = new BlockPos(x, y, z);
        universe = buffer.readInt();
        ipAddress = buffer.readUtf(32767);
    }

    private final BlockPos blockPos;
    private final int universe;
    private final String ipAddress;

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getUniverse() {
        return universe;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(universe);
        buf.writeUtf(ipAddress);
    }

    public void handle(Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleUpdateArtNetInterface(context.get(), blockPos, universe, ipAddress));
        context.get().setPacketHandled(true);
    }

}
