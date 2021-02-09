package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class UpdateFixturePacket {

    public UpdateFixturePacket(BlockPos blockPos, int tilt, int pan) {
        this.blockPos = blockPos;
        this.tilt = tilt;
        this.pan = pan;
    }

    public UpdateFixturePacket(PacketBuffer buffer) {
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

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(tilt);
        buf.writeInt(pan);
    }

    public void handle(Supplier<Context> context) {
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleUpdateFixture(context.get(), blockPos, pan, tilt));
        context.get().setPacketHandled(true);
    }

}
