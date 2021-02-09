package dev.theatricalmod.theatrical.network.control;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleModePacket {
        public ToggleModePacket(BlockPos blockPos) {
            this.blockPos = blockPos;
        }

        public ToggleModePacket(PacketBuffer buffer) {
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();
            blockPos = new BlockPos(x, y, z);
        }

        private final BlockPos blockPos;

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public void toBytes(PacketBuffer buf) {
            buf.writeInt(blockPos.getX());
            buf.writeInt(blockPos.getY());
            buf.writeInt(blockPos.getZ());
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> TheatricalMod.proxy.handleToggleMode(context.get(), blockPos));
            context.get().setPacketHandled(true);
        }

}
