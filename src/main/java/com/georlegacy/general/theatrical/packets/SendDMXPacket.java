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
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendDMXPacket implements IMessage {

    public SendDMXPacket() {
    }


    public SendDMXPacket(BlockPos blockPos, byte[] data) {
        this.blockPos = blockPos;
        this.data = data;
    }

    private BlockPos blockPos;
    private byte[] data;

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        blockPos = new BlockPos(x, y, z);
        int i = buf.readInt();
        if(data == null){
            data = new byte[i];
        }
        buf.readBytes(data);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static class ClientHandler implements IMessageHandler<SendDMXPacket, IMessage>{

        @Override
        public IMessage onMessage(SendDMXPacket message, MessageContext ctx) {
            doTheFuckingThing(message.getBlockPos(), message.getData(), ctx);
            return null;
        }

        private void doTheFuckingThing(BlockPos pos, byte[] data, MessageContext ctx){
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                TileEntity tileFresnel = TheatricalMain.proxy.getWorld().getTileEntity(pos);
                if(tileFresnel != null) {
                    IDMXReceiver dmxReceiver = tileFresnel.getCapability(DMXReceiver.CAP, EnumFacing.NORTH);
                    if (dmxReceiver != null) {
                        if (data != null) {
                            for (int i = 0; i < data.length; i++) {
                                dmxReceiver
                                    .updateChannel(i, data[i]);
                            }
                        }
                    }
                    tileFresnel.markDirty();
                }
                TheatricalMain.proxy.getWorld().notifyBlockUpdate(pos, TheatricalMain.proxy.getWorld().getBlockState(pos), TheatricalMain.proxy.getWorld().getBlockState(pos), 11);
                TheatricalMain.proxy.getWorld().markChunkDirty(pos, tileFresnel);
            });
        }
    }

}
