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
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.tiles.interfaces.TileArtNetInterface;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateArtNetInterfacePacket implements IMessage {

    public UpdateArtNetInterfacePacket() {
    }


    public UpdateArtNetInterfacePacket(int subnet, int universe, String ip, BlockPos blockPos) {
        this.subnet = subnet;
        this.universe = universe;
        this.ip = ip;
        this.pos = blockPos;
    }

    private BlockPos pos;
    private int subnet;
    private int universe;
    private String ip;

    public BlockPos getPos() {
        return pos;
    }

    public int getSubnet() {
        return subnet;
    }

    public int getUniverse() {
        return universe;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        subnet = buf.readInt();
        universe = buf.readInt();
        ip = ByteBufUtils.readUTF8String(buf);
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(subnet);
        buf.writeInt(universe);
        ByteBufUtils.writeUTF8String(buf, ip);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static class ServerHandler implements IMessageHandler<UpdateArtNetInterfacePacket, IMessage>{

        @Override
        public IMessage onMessage(UpdateArtNetInterfacePacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(UpdateArtNetInterfacePacket message, MessageContext ctx){
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.world;
                BlockPos blockPos = message.getPos();
                TileArtNetInterface tileFresnel = (TileArtNetInterface) world
                    .getTileEntity(blockPos);
                tileFresnel.setSubnet(message.getSubnet());
                tileFresnel.setUniverse(message.getUniverse());
                tileFresnel.setIp(message.getIp());
                world.markChunkDirty(blockPos, tileFresnel);
                TheatricalPacketHandler.INSTANCE.sendToAll(
                    new UpdateArtNetInterfacePacket(message.getSubnet(), message.getUniverse(), message.getIp(), tileFresnel.getPos()));
            });
        }
    }

    public static class ClientHandler implements IMessageHandler<UpdateArtNetInterfacePacket, IMessage>{

        @Override
        public IMessage onMessage(UpdateArtNetInterfacePacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(UpdateArtNetInterfacePacket message, MessageContext ctx){
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                World world = TheatricalMain.proxy.getWorld();
                BlockPos blockPos = message.getPos();
                TileArtNetInterface tileFresnel = (TileArtNetInterface) world.getTileEntity(blockPos);
                tileFresnel.setSubnet(message.getSubnet());
                tileFresnel.setUniverse(message.getUniverse());
                tileFresnel.setIp(message.getIp());
                world.markChunkDirty(blockPos, tileFresnel);
            });
        }
    }
}
