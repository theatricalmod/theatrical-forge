package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class UpdateDMXAddressPacket {

    public UpdateDMXAddressPacket(BlockPos blockPos, int address) {
        this.blockPos = blockPos;
        this.address = address;
    }

    public UpdateDMXAddressPacket(PacketBuffer buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        blockPos = new BlockPos(x, y, z);
        address = buffer.readInt();
    }

    private final BlockPos blockPos;
    private final int address;

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getAddress() {
        return address;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(address);
    }

    public void handle(Supplier<Context> context) {
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleUpdateDMXAddress(context.get(), blockPos, address));
        context.get().setPacketHandled(true);
    }

}
