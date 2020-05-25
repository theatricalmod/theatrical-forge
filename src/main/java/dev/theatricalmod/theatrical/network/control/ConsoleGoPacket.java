package dev.theatricalmod.theatrical.network.control;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ConsoleGoPacket {

    public ConsoleGoPacket(BlockPos blockPos, int fadeInTicks, int fadeOutTicks) {
        this.blockPos = blockPos;
        this.fadeInTicks = fadeInTicks;
        this.fadeOutTicks = fadeOutTicks;
    }

    public ConsoleGoPacket(PacketBuffer buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        blockPos = new BlockPos(x, y, z);
        fadeInTicks = buffer.readInt();
        fadeOutTicks = buffer.readInt();
    }

    private BlockPos blockPos;
    private int fadeInTicks,fadeOutTicks;

    public BlockPos getBlockPos() {
        return blockPos;
    }


    public void toBytes(PacketBuffer buf) {
            buf.writeInt(blockPos.getX());
            buf.writeInt(blockPos.getY());
            buf.writeInt(blockPos.getZ());
            buf.writeInt(fadeInTicks);
            buf.writeInt(fadeOutTicks);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleConsoleGo(context.get(), blockPos, fadeInTicks, fadeOutTicks));
        context.get().setPacketHandled(true);
    }

}
