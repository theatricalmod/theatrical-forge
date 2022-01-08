package dev.theatricalmod.theatrical.tiles.sound;

import dev.theatricalmod.theatrical.api.sound.ISoundEmitter;
import dev.theatricalmod.theatrical.network.SendAudioDataPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Play out -> Mixer -> Amplifier -> Speaker
 * Play out: SOUND
 * Mixer: VOLUME
 * Amplifier: VOLUME * number
 * Speaker: actually plays
 */

/**
 * Passive Speaker, requires input to be amplified prior
 */
public class TileEntitySpeaker extends TileEntityTheatricalBase implements ITickableTileEntity, ISoundEmitter {

    private AudioStreamManager audioStreamManager;
    private IAudioStream audioStream;
    private int defaultAnInt, packetID = 0;
    private int audioID = -1;
    private ChannelProcessor processor;

    public TileEntitySpeaker() {
        super(TheatricalTiles.SPEAKER.get());
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        if(audioStream != null) {
            List<ByteBuffer> byteBuffers = this.readFromStream(audioStream, defaultAnInt, 1);
            if (byteBuffers.size() > 0) {
                if(byteBuffers.stream().map(ByteBuffer::limit).reduce(0, Integer::sum) > 0) {
                    for (ByteBuffer bytebuffer : byteBuffers) {
                        byte[] asBytes = new byte[bytebuffer.remaining()];
                        bytebuffer.get(asBytes);
                        short[] shorts = new short[asBytes.length/2];
                        ByteBuffer.wrap(asBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
                        processor.process(shorts, 0, shorts.length);
                        byte[] bytes = new byte[Float.BYTES * shorts.length];
                        ByteBuffer.wrap(bytes).asShortBuffer().put(shorts);
                        bytebuffer = ByteBuffer.wrap(bytes);
                        // Send over network
                        TheatricalNetworkHandler.MAIN.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 10d, world.getDimensionKey())), new SendAudioDataPacket(audioID, packetID, audioStream.getAudioFormat().getSampleRate(), audioStream.getAudioFormat().getChannels(), bytebuffer));
                    }
                } else {
                    audioID = -1;
                    audioStream = null;
                }
            }
        }
    }

    /**
     * Play a sound from the speaker
     * @param sound -> SoundEvent
     * @param volume -> Amplified volume in dB
     */
    public void playSound(SoundEvent sound, float volume) {
        float minecraftVolume = dBToMinecraft(volume);
        this.world.playSound(null, this.getPos(), sound, SoundCategory.NEUTRAL, minecraftVolume, 1.0f);
    }

    /**
     * Convert DB to Minecraft sound volume
     * 10f = 5
     * -40 = 0
     * @param dB - dB to be converted
     * @return - the converted minecraft level
     */
    private float dBToMinecraft(float dB) {
        return (dB + 40f) / 10f;
    }

    private static int getSampleSize(AudioFormat audioFormat, int sampleAmount) {
        return (int)((float)(sampleAmount * audioFormat.getSampleSizeInBits()) / 8.0F * (float)audioFormat.getChannels() * audioFormat.getSampleRate());
    }

    public void test(){
        if(world.isRemote){
            return;
        }
        if(audioID != -1){
            return;
        }
        audioID = new Random().nextInt(100);
        if(audioStreamManager == null){
            audioStreamManager = new AudioStreamManager(Minecraft.getInstance().getResourceManager());
        }
        SimpleSound sound = new SimpleSound(SoundEvents.MUSIC_DISC_11, SoundCategory.RECORDS, 1.0f, 1.0f, this.getPos());
        SoundEventAccessor soundeventaccessor = sound.createAccessor(Minecraft.getInstance().getSoundHandler());
        processor = new ChannelProcessor(new float[15]);
        this.audioStreamManager.createStreamingResource(sound.getSound().getSoundAsOggLocation(), true).thenAccept((audioStream) -> {
            AudioFormat audioformat = audioStream.getAudioFormat();
            defaultAnInt = getSampleSize(audioformat, 1);
            List<ByteBuffer> byteBuffers = this.readFromStream(audioStream, defaultAnInt, 8);
            if(byteBuffers.size() > 0) {
                for(ByteBuffer bytebuffer : byteBuffers) {
                    try {
                        byte[] asBytes = new byte[bytebuffer.remaining()];
                        bytebuffer.get(asBytes);
                        short[] shorts = new short[asBytes.length/2];
                        ByteBuffer.wrap(asBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
                        processor.process(shorts, 0, shorts.length);
                        byte[] bytes = new byte[Float.BYTES * shorts.length];
                        ByteBuffer.wrap(bytes).asShortBuffer().put(shorts);
                        bytebuffer = ByteBuffer.wrap(bytes);
                        // Send over network
                        TheatricalNetworkHandler.MAIN.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 10d, world.getDimensionKey())), new SendAudioDataPacket(audioID, packetID, audioformat.getSampleRate(), audioformat.getChannels(), bytebuffer));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.audioStream  = audioStream;
        });
    }


    protected static float[] bytesToFloats(byte[] bytes) {

        if (bytes.length % Float.BYTES != 0) {
            throw new RuntimeException("Illegal length");
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        FloatBuffer fb = buffer.asFloatBuffer();
        float[] floats = new float[bytes.length / Float.BYTES];

        fb.get(floats);

        return floats;

    }
    private List<ByteBuffer> readFromStream(IAudioStream audioStream, int defaultByteBufferCapacity, int readCount) {
        List<ByteBuffer> buffers = new ArrayList<>();
        if (audioStream != null) {
            try {
                for(int i = 0; i < readCount; ++i) {
                    ByteBuffer bytebuffer = audioStream.readOggSoundWithCapacity(defaultByteBufferCapacity);
                    buffers.add(bytebuffer);
                }
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
//                LOGGER.error("Failed to read from audio stream", (Throwable)ioexception);
            }
        }
        return buffers;
    }

    private static final Coefficients[] coefficients48000 = {
            new Coefficients(9.9847546664e-01f, 7.6226668143e-04f, 1.9984647656e+00f),
            new Coefficients(9.9756184654e-01f, 1.2190767289e-03f, 1.9975344645e+00f),
            new Coefficients(9.9616261379e-01f, 1.9186931041e-03f, 1.9960947369e+00f),
            new Coefficients(9.9391578543e-01f, 3.0421072865e-03f, 1.9937449618e+00f),
            new Coefficients(9.9028307215e-01f, 4.8584639242e-03f, 1.9898465702e+00f),
            new Coefficients(9.8485897264e-01f, 7.5705136795e-03f, 1.9837962543e+00f),
            new Coefficients(9.7588512657e-01f, 1.2057436715e-02f, 1.9731772447e+00f),
            new Coefficients(9.6228521814e-01f, 1.8857390928e-02f, 1.9556164694e+00f),
            new Coefficients(9.4080933132e-01f, 2.9595334338e-02f, 1.9242054384e+00f),
            new Coefficients(9.0702059196e-01f, 4.6489704022e-02f, 1.8653476166e+00f),
            new Coefficients(8.5868004289e-01f, 7.0659978553e-02f, 1.7600401337e+00f),
            new Coefficients(7.8409610788e-01f, 1.0795194606e-01f, 1.5450725522e+00f),
            new Coefficients(6.8332861002e-01f, 1.5833569499e-01f, 1.1426447155e+00f),
            new Coefficients(5.5267518228e-01f, 2.2366240886e-01f, 4.0186190803e-01f),
            new Coefficients(4.1811888447e-01f, 2.9094055777e-01f, -7.0905944223e-01f)
    };

    private static class Coefficients {
        private final float beta;
        private final float alpha;
        private final float gamma;

        private Coefficients(float beta, float alpha, float gamma) {
            this.beta = beta;
            this.alpha = alpha;
            this.gamma = gamma;
        }
    }


    private static class ChannelProcessor {
        private final float[] history;
        private final float[] bandMultipliers;

        private int current;
        private int minusOne;
        private int minusTwo;

        private ChannelProcessor(float[] bandMultipliers) {
            this.history = new float[15 * 6];
            this.bandMultipliers = bandMultipliers;
            this.current = 0;
            this.minusOne = 2;
            this.minusTwo = 1;
        }

        private void process(short[] samples, int startIndex, int endIndex) {
            for (int sampleIndex = startIndex; sampleIndex < endIndex; sampleIndex++) {
                float sample = samples[sampleIndex];
                float result = sample * 0.25f;

                for (int bandIndex = 0; bandIndex < 15; bandIndex++) {
                    int x = bandIndex * 6;
                    int y = x + 3;

                    Coefficients coefficients = coefficients48000[bandIndex];

                    float bandResult = coefficients.alpha * (sample - history[x + minusTwo]) +
                            coefficients.gamma * history[y + minusOne] -
                            coefficients.beta * history[y + minusTwo];

                    history[x + current] = sample;
                    history[y + current] = bandResult;

                    result += bandResult * bandMultipliers[bandIndex];
                }

                float min = Math.min(Math.max(result * 4.0f, -1.0f), 1.0f);
                int i = MathHelper.clamp((int)(min * 32767.5F - 0.5F), -32768, 32767);
                samples[sampleIndex] = (short) i;

                if (++current == 3) {
                    current = 0;
                }

                if (++minusOne == 3) {
                    minusOne = 0;
                }

                if (++minusTwo == 3) {
                    minusTwo = 0;
                }
            }
        }

        private void reset() {
            Arrays.fill(history, 0.0f);
        }
    }

}
