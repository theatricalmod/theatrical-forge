package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class TheatricalNetworkHandler {

    public static SimpleChannel MAIN;
    private static final String MAIN_VERSION = "1";

    public static void init(){
        MAIN = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(TheatricalMod.MOD_ID, "main")).clientAcceptedVersions(MAIN_VERSION::equals).serverAcceptedVersions(MAIN_VERSION::equals).networkProtocolVersion(() -> MAIN_VERSION).simpleChannel();

        MAIN.registerMessage(1, SendDMXPacket.class, SendDMXPacket::toBytes, SendDMXPacket::new, SendDMXPacket::handle);
        MAIN.registerMessage(2, SendDMXProviderPacket.class, SendDMXProviderPacket::toBytes, SendDMXProviderPacket::new, SendDMXProviderPacket::handle);
    }

}
