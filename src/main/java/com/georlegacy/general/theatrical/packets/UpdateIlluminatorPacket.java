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

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class UpdateIlluminatorPacket implements IMessage {

    public UpdateIlluminatorPacket() {
    }


    public UpdateIlluminatorPacket(BlockPos illuminator, BlockPos controller) {
        this.illuminator = illuminator;
        this.controller = controller;
    }

    private BlockPos illuminator;
    private BlockPos controller;

    public BlockPos getIlluminator() {
        return illuminator;
    }

    public BlockPos getController() {
        return controller;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        illuminator = new BlockPos(x, y, z);
        int x2 = buf.readInt();
        int y2 = buf.readInt();
        int z2 = buf.readInt();
        controller = new BlockPos(x2, y2, z2);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(illuminator.getX());
        buf.writeInt(illuminator.getY());
        buf.writeInt(illuminator.getZ());
        buf.writeInt(controller.getX());
        buf.writeInt(controller.getY());
        buf.writeInt(controller.getZ());
    }

}
