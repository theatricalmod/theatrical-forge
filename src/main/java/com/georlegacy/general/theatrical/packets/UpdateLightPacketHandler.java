package com.georlegacy.general.theatrical.packets;

import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdateLightPacketHandler implements IMessageHandler<UpdateLightPacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(UpdateLightPacket message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().world.markChunkDirty(new BlockPos(message.getTag().getInteger("x"),message.getTag().getInteger("y") , message.getTag().getInteger("z")), null);
        });
        return null;
    }
}
