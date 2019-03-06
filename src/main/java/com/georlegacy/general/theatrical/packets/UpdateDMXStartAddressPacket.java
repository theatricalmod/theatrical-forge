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

}
