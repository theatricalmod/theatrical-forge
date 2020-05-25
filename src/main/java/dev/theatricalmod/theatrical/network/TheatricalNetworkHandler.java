package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.network.control.ConsoleGoPacket;
import dev.theatricalmod.theatrical.network.control.MoveStepPacket;
import dev.theatricalmod.theatrical.network.control.ToggleModePacket;
import dev.theatricalmod.theatrical.network.control.UpdateConsoleFaderPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class TheatricalNetworkHandler {

    public static SimpleChannel MAIN;
    private static final String MAIN_VERSION = "1";

    public static void init(){
        MAIN = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(TheatricalMod.MOD_ID, "main")).clientAcceptedVersions(MAIN_VERSION::equals).serverAcceptedVersions(MAIN_VERSION::equals).networkProtocolVersion(() -> MAIN_VERSION).simpleChannel();

        MAIN.registerMessage(2, SendDMXProviderPacket.class, SendDMXProviderPacket::toBytes, SendDMXProviderPacket::new, SendDMXProviderPacket::handle);
        MAIN.registerMessage(3, UpdateDMXAddressPacket.class, UpdateDMXAddressPacket::toBytes, UpdateDMXAddressPacket::new, UpdateDMXAddressPacket::handle);
        MAIN.registerMessage(4, UpdateArtNetInterfacePacket.class, UpdateArtNetInterfacePacket::toBytes, UpdateArtNetInterfacePacket::new, UpdateArtNetInterfacePacket::handle);
        MAIN.registerMessage(5, UpdateFixturePacket.class, UpdateFixturePacket::toBytes, UpdateFixturePacket::new, UpdateFixturePacket::handle);
        MAIN.registerMessage(6, ChangeDimmerPatchPacket.class, ChangeDimmerPatchPacket::toBytes, ChangeDimmerPatchPacket::new, ChangeDimmerPatchPacket::handle);
        MAIN.registerMessage(7, UpdateConsoleFaderPacket.class, UpdateConsoleFaderPacket::toBytes, UpdateConsoleFaderPacket::new, UpdateConsoleFaderPacket::handle);
        MAIN.registerMessage(8, ConsoleGoPacket.class, ConsoleGoPacket::toBytes, ConsoleGoPacket::new, ConsoleGoPacket::handle);
        MAIN.registerMessage(9, MoveStepPacket.class, MoveStepPacket::toBytes, MoveStepPacket::new, MoveStepPacket::handle);
        MAIN.registerMessage(10, ToggleModePacket.class, ToggleModePacket::toBytes, ToggleModePacket::new, ToggleModePacket::handle);
    }
}
