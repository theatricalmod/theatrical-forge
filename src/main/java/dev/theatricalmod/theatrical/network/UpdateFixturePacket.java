package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateFixturePacket {

    public UpdateFixturePacket(BlockPos blockPos, int tilt, int pan) {
        this.blockPos = blockPos;
        this.tilt = tilt;
        this.pan = pan;
    }

    public UpdateFixturePacket(FriendlyByteBuf buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        blockPos = new BlockPos(x, y, z);
        tilt = buffer.readInt();
        pan = buffer.readInt();
    }

    private final BlockPos blockPos;
    private final int tilt;
    private final int pan;

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getTilt() {
        return tilt;
    }

    public int getPan() {
        return pan;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(tilt);
        buf.writeInt(pan);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleUpdateFixture(context.get(), blockPos, pan, tilt));
        context.get().setPacketHandled(true);
    }

}
