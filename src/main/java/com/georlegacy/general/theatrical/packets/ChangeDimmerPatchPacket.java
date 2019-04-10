/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.packets;

import com.georlegacy.general.theatrical.TheatricalMain;
import com.georlegacy.general.theatrical.api.capabilities.socapex.ISocapexProvider;
import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexProvider;
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.tiles.dimming.TileDimmerRack;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChangeDimmerPatchPacket implements IMessage {

    public ChangeDimmerPatchPacket() {
    }


    public ChangeDimmerPatchPacket(BlockPos blockPos, int channel, String patch) {
        this.pos = blockPos;
        this.channel = channel;
        this.patch = patch;
    }

    private BlockPos pos;
    private int channel;
    private String patch;

    public BlockPos getPos() {
        return pos;
    }

    public int getChannel() {
        return channel;
    }

    public String getPatch() {
        return patch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        channel = buf.readInt();
        patch = ByteBufUtils.readUTF8String(buf);
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(channel);
        ByteBufUtils.writeUTF8String(buf, patch);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static class ServerHandler implements IMessageHandler<ChangeDimmerPatchPacket, IMessage> {

        @Override
        public IMessage onMessage(ChangeDimmerPatchPacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(ChangeDimmerPatchPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.world;
                BlockPos blockPos = message.getPos();
                TileDimmerRack tileDimmerRack = (TileDimmerRack) world
                    .getTileEntity(blockPos);
                ISocapexProvider socapexProvider = tileDimmerRack.getCapability(SocapexProvider.CAP, null);
                if (socapexProvider != null) {
                    socapexProvider.patch(message.getChannel(), message.getPatch());
                }
                world.markChunkDirty(blockPos, tileDimmerRack);
                TheatricalPacketHandler.INSTANCE.sendToAll(
                    new ChangeDimmerPatchPacket(message.getPos(), message.getChannel(), message.getPatch()));
            });
        }
    }

    public static class ClientHandler implements IMessageHandler<ChangeDimmerPatchPacket, IMessage> {

        @Override
        public IMessage onMessage(ChangeDimmerPatchPacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(ChangeDimmerPatchPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                World world = TheatricalMain.proxy.getWorld();
                BlockPos blockPos = message.getPos();
                TileDimmerRack tileDimmerRack = (TileDimmerRack) world
                    .getTileEntity(blockPos);
                ISocapexProvider socapexProvider = tileDimmerRack.getCapability(SocapexProvider.CAP, null);
                if (socapexProvider != null) {
                    socapexProvider.patch(message.getChannel(), message.getPatch());
                }
                world.markChunkDirty(blockPos, tileDimmerRack);
            });
        }
    }
}