package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateDMXAddressPacket {

    public UpdateDMXAddressPacket(BlockPos blockPos, int address) {
        this.blockPos = blockPos;
        this.address = address;
    }

    public UpdateDMXAddressPacket(FriendlyByteBuf buffer) {
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

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(address);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleUpdateDMXAddress(context.get(), blockPos, address));
        context.get().setPacketHandled(true);
    }

}
