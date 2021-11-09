package dev.theatricalmod.theatrical.network;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public class SendAudioDataPacket {

    private ByteBuffer audioData;
    private int audioID, numChannels;
    private float sampleRate;

    public SendAudioDataPacket(int audioID, float sampleRate, int numChannels, ByteBuffer buffer) {
        this.audioID = audioID;
        this.sampleRate = sampleRate;
        this.numChannels = numChannels;
        this.audioData = buffer;
    }

    public SendAudioDataPacket(PacketBuffer buffer){
        audioID = buffer.readInt();
        sampleRate = buffer.readFloat();
        numChannels = buffer.readInt();
        int i = buffer.readInt();
        audioData = ByteBuffer.allocate(i);
        buffer.readBytes(audioData);
    }
    public ByteBuffer getData() {
        return audioData;
    }

    public int getAudioID() {
        return audioID;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(audioID);
        buf.writeFloat(sampleRate);
        buf.writeInt(numChannels);
        buf.writeInt(audioData.limit());
        buf.writeBytes(audioData);
    }

    public void handle(Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> TheatricalMod.proxy.handleAudioData(audioID, sampleRate, numChannels, audioData));
        context.get().setPacketHandled(true);
    }

}
