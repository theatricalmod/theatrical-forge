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

package com.georlegacy.general.theatrical.packets.handlers.server;

import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.packets.UpdateArtNetInterfacePacket;
import com.georlegacy.general.theatrical.tiles.interfaces.TileArtNetInterface;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateArtNetInterfaceServerHandler implements IMessageHandler<UpdateArtNetInterfacePacket, IMessage> {

    @Override
    public IMessage onMessage(UpdateArtNetInterfacePacket message, MessageContext ctx) {
        ctx.getServerHandler().player.server.addScheduledTask(() -> {
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
        return null;
    }

}
