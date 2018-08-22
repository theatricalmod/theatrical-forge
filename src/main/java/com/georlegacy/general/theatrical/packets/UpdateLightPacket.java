package com.georlegacy.general.theatrical.packets;

import io.netty.buffer.ByteBuf;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class UpdateLightPacket implements IMessage {

    private NBTTagCompound tag;

    public UpdateLightPacket(){

    }

    public UpdateLightPacket(NBTTagCompound tag){
        this.tag = tag;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tag);
    }
}
