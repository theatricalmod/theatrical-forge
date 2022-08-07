package dev.theatricalmod.theatrical.network.control;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateConsoleFaderPacket {
        public UpdateConsoleFaderPacket(BlockPos blockPos, int fader, int value) {
            this.blockPos = blockPos;
            this.fader = fader;
            this.value = value;
        }

        public UpdateConsoleFaderPacket(FriendlyByteBuf buffer) {
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

        public void toBytes(FriendlyByteBuf buf) {
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
