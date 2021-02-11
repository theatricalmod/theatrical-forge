package dev.theatricalmod.theatrical.network.control;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateConsoleFaderPacket {
        public UpdateConsoleFaderPacket(BlockPos blockPos, int fader, int value) {
            this.blockPos = blockPos;
            this.fader = fader;
            this.value = value;
        }

        public UpdateConsoleFaderPacket(PacketBuffer buffer) {
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();
            blockPos = new BlockPos(x, y, z);
            fader = buffer.readInt();
            value = buffer.readInt();
        }

        private final BlockPos blockPos;
        private final int fader;
    private final int value;

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public void toBytes(PacketBuffer buf) {
            buf.writeInt(blockPos.getX());
            buf.writeInt(blockPos.getY());
            buf.writeInt(blockPos.getZ());
            buf.writeInt(fader);
            buf.writeInt(value);
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> TheatricalMod.proxy.handleConsoleFaderUpdate(context.get(), blockPos, fader, value));
            context.get().setPacketHandled(true);
        }

}
