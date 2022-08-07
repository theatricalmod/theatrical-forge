package dev.theatricalmod.theatrical.network.control;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MoveStepPacket {
        public MoveStepPacket(BlockPos blockPos, boolean isForwards) {
            this.blockPos = blockPos;
            this.isForwards = isForwards;
        }

        public MoveStepPacket(FriendlyByteBuf buffer) {
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();
            blockPos = new BlockPos(x, y, z);
            isForwards = buffer.readBoolean();
        }

        private final BlockPos blockPos;
        private final boolean isForwards;

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public void toBytes(FriendlyByteBuf buf) {
            buf.writeInt(blockPos.getX());
            buf.writeInt(blockPos.getY());
            buf.writeInt(blockPos.getZ());
            buf.writeBoolean(isForwards);
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> TheatricalMod.proxy.handleMoveStep(context.get(), blockPos, isForwards));
            context.get().setPacketHandled(true);
        }

}
