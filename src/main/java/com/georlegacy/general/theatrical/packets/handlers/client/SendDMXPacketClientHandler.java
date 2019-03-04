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

package com.georlegacy.general.theatrical.packets.handlers.client;

import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.packets.SendDMXPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SendDMXPacketClientHandler implements IMessageHandler<SendDMXPacket, IMessage> {

    @Override
    public IMessage onMessage(SendDMXPacket message, MessageContext ctx) {
        if(ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                BlockPos blockPos = message.getBlockPos();
                TileEntity tileFresnel = Minecraft
                    .getMinecraft().world.getTileEntity(blockPos);
                if (tileFresnel.hasCapability(DMXReceiver.CAP, EnumFacing.NORTH)) {
                    if(message.getData() != null){
                        for(int i = 0; i < message.getData().length; i++){
                            tileFresnel.getCapability(DMXReceiver.CAP, EnumFacing.NORTH).updateChannel(i, message.getData()[i]);
                        }
                    }
                }
                tileFresnel.markDirty();
                world.notifyBlockUpdate(blockPos, world.getBlockState(blockPos), world.getBlockState(blockPos), 11);
                Minecraft.getMinecraft().world.markChunkDirty(blockPos, tileFresnel);
            });
        }
        return null;
    }

}
