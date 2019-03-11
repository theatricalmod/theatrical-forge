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

import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.tiles.TileDMXAcceptor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateDMXStartAddressPacket implements IMessage {

    public UpdateDMXStartAddressPacket() {
    }


    public UpdateDMXStartAddressPacket(int dmxStartPoint, BlockPos blockPos) {
        this.dmxStartPoint = dmxStartPoint;
        this.pos = blockPos;
    }

    private BlockPos pos;
    private int dmxStartPoint;

    public BlockPos getPos() {
        return pos;
    }

    public int getDmxStartPoint() {
        return dmxStartPoint;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dmxStartPoint = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dmxStartPoint);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }


    public static class ServerHandler implements IMessageHandler<UpdateDMXStartAddressPacket, IMessage>{

        @Override
        public IMessage onMessage(UpdateDMXStartAddressPacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(UpdateDMXStartAddressPacket message, MessageContext ctx){
            ctx.getServerHandler().player.server.addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.world;
                BlockPos blockPos = message.getPos();
                TileDMXAcceptor tileFresnel = (TileDMXAcceptor) world
                    .getTileEntity(blockPos);
                if(tileFresnel.hasCapability(DMXReceiver.CAP, EnumFacing.SOUTH)){
                    tileFresnel.getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).setDMXStartPoint(message.getDmxStartPoint());
                }
                world.markChunkDirty(blockPos, tileFresnel);
                TheatricalPacketHandler.INSTANCE.sendToAll(
                    new UpdateDMXStartAddressPacket(message.getDmxStartPoint(), tileFresnel.getPos()));
            });
        }
    }

    public static class ClientHandler implements
        IMessageHandler<UpdateDMXStartAddressPacket, IMessage> {

        @Override
        public IMessage onMessage(UpdateDMXStartAddressPacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(UpdateDMXStartAddressPacket message, MessageContext ctx){
            Minecraft.getMinecraft().addScheduledTask(() -> {
                BlockPos blockPos = message.getPos();
                TileDMXAcceptor tileFresnel = (TileDMXAcceptor) Minecraft
                    .getMinecraft().world.getTileEntity(blockPos);
                if(tileFresnel.hasCapability(DMXReceiver.CAP, EnumFacing.SOUTH)){
                    tileFresnel.getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).setDMXStartPoint(message.getDmxStartPoint());
                }
                Minecraft.getMinecraft().world.markChunkDirty(blockPos, tileFresnel);
            });
        }
    }
}
