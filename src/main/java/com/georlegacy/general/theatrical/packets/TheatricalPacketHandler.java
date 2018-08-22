package com.georlegacy.general.theatrical.packets;

import com.georlegacy.general.theatrical.util.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class TheatricalPacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(
        Reference.MOD_ID);

}
