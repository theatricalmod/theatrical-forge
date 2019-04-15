package dev.theatricalmod.theatrical.util;

import ch.bildspur.artnet.ArtNetClient;

public class ArtnetThread extends Thread {


    private String ip;
    private ArtNetClient client;

    public ArtnetThread(String ip, ArtNetClient artNetClient){
        this.ip = ip;
        client = artNetClient;
        setDaemon(true);
    }


    @Override
    public void run() {
        client.start(ip);
    }
}
