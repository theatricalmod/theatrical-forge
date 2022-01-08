package dev.theatricalmod.theatrical.client.sound;

import com.google.common.collect.Lists;
import net.minecraft.client.audio.IAudioStream;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class NetworkedAudioStream implements IAudioStream {

    private NetworkedInputStream inputStream;
    private ByteBuffer buffer = MemoryUtil.memAlloc(8192);
    private AudioFormat audioFormat;

    public NetworkedAudioStream(AudioFormat audioFormat, ByteBuffer initialData) {
        this.audioFormat = audioFormat;
        ((java.nio.Buffer)this.buffer).limit(0);
        this.inputStream = new NetworkedInputStream(initialData);
        try {
            this.readFromInputStream();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addData(ByteBuffer buffer) {
        this.inputStream.addData(buffer);
    }

    public boolean readFromInputStream() throws IOException {
        int i = this.buffer.limit();
        int j = this.buffer.capacity() - i;
        if (j == 0) {
            return true;
        } else {
            byte[] abyte = new byte[j];
            int k = this.inputStream.read(abyte);
            if (k == -1) {
                return false;
            } else {
                int l = this.buffer.position();
                ((java.nio.Buffer)this.buffer).limit(i + k);
                ((java.nio.Buffer)this.buffer).position(i);
                this.buffer.put(abyte, 0, k);
                ((java.nio.Buffer)this.buffer).position(l);
                return true;
            }
        }
    }

    public void clearInputBuffer(){
        boolean flag = this.buffer.position() == 0;
        boolean flag1 = this.buffer.position() == this.buffer.limit();
        if (flag1 && !flag) {
            ((java.nio.Buffer)this.buffer).position(0);
            ((java.nio.Buffer)this.buffer).limit(0);
        } else {
            ByteBuffer bytebuffer = MemoryUtil.memAlloc(flag ? 2 * this.buffer.capacity() : this.buffer.capacity());
            bytebuffer.put(this.buffer);
            MemoryUtil.memFree(this.buffer);
            ((java.nio.Buffer)bytebuffer).flip();
            this.buffer = bytebuffer;
        }
    }

    public boolean read(Buffer buffer) throws IOException {
        if(this.buffer.remaining() == 0) {
            this.clearInputBuffer();
            if(!this.readFromInputStream()) {
                return false;
            }
        }
        buffer.appendOggAudioBytes(this.buffer.get());
        return true;
    }

    @Override
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    @Override
    public ByteBuffer readOggSoundWithCapacity(int size) throws IOException {
        Buffer buffer = new Buffer(size + 8192);

        while(this.read(buffer) && buffer.filledBytes < size) {
        }

        return buffer.mergeBuffers();
    }

    @Override
    public void close() throws IOException {

    }


    static class Buffer {
        private final List<ByteBuffer> storedBuffers = Lists.newArrayList();
        private final int bufferCapacity;
        private int filledBytes;
        private ByteBuffer currentBuffer;

        public Buffer(int capacity) {
            this.bufferCapacity = capacity + 1 & -2;
            this.createBuffer();
        }

        private void createBuffer() {
            this.currentBuffer = BufferUtils.createByteBuffer(this.bufferCapacity);
        }

        public void appendOggAudioBytes(byte b) {
            if (this.currentBuffer.remaining() == 0) {
                ((java.nio.Buffer)this.currentBuffer).flip();
                this.storedBuffers.add(this.currentBuffer);
                this.createBuffer();
            }
            this.currentBuffer.put(b);
            this.filledBytes += 1;
        }

        public ByteBuffer mergeBuffers() {
            ((java.nio.Buffer)this.currentBuffer).flip();
            if (this.storedBuffers.isEmpty()) {
                return this.currentBuffer;
            } else {
                ByteBuffer bytebuffer = BufferUtils.createByteBuffer(this.filledBytes);
                this.storedBuffers.forEach(bytebuffer::put);
                bytebuffer.put(this.currentBuffer);
                ((java.nio.Buffer)bytebuffer).flip();
                return bytebuffer;
            }
        }
    }
}
