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
import com.georlegacy.general.theatrical.packets.UpdateIlluminatorPacket;
import com.georlegacy.general.theatrical.tiles.TileIlluminator;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateIlluminatorPacketServerHandler implements
    IMessageHandler<UpdateIlluminatorPacket, IMessage> {

    @Override
    public IMessage onMessage(UpdateIlluminatorPacket message, MessageContext ctx) {
        ctx.getServerHandler().player.server.addScheduledTask(() -> {
            BlockPos blockPos = message.getIlluminator();
            TileIlluminator tileIlluminator = (TileIlluminator) ctx.getServerHandler().player
                .getServerWorld().getTileEntity(blockPos);
            if (tileIlluminator == null) {
                return;
            }
            tileIlluminator.setController(message.getController());
            ctx.getServerHandler().player
                .getServerWorld().markChunkDirty(blockPos, tileIlluminator);
            TheatricalPacketHandler.INSTANCE.sendToAll(
                new UpdateIlluminatorPacket(message.getIlluminator(), message.getController()));
        });
        return null;
    }

}
