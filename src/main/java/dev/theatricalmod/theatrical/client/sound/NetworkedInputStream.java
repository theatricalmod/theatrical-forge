package dev.theatricalmod.theatrical.client.sound;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class NetworkedInputStream extends InputStream {

    private ByteBuffer buffer;
    private List<ByteBuffer> waitingBuffers;

    public NetworkedInputStream(ByteBuffer initialData) {
        buffer = initialData;
        waitingBuffers = new ArrayList<>();
    }

    public void addData(ByteBuffer data) {
        waitingBuffers.add(data);
    }

    @Override
    public int read() throws IOException {
        if(buffer.remaining() == 0){
            buffer = waitingBuffers.remove(0);
        }
        return buffer.get() & 0xff;
    }


}
