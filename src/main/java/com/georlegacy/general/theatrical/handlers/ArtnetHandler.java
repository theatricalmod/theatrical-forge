package com.georlegacy.general.theatrical.handlers;

import ch.bildspur.artnet.ArtNetClient;
import net.minecraft.client.Minecraft;

public class ArtnetHandler {

    private static ArtNetClient client;

    public static ArtNetClient getClient(){
        if (client == null){
            client = new ArtNetClient();
            startClient();
        }
        return client;
    }

    public static void startClient(){
        Minecraft.getMinecraft().addScheduledTask(() -> {
            client.start();
        });
    }

    public static void stopClient(){
        client.stop();
    }

}
