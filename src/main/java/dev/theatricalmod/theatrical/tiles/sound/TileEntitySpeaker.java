package dev.theatricalmod.theatrical.tiles.sound;

import dev.theatricalmod.theatrical.api.sound.ISoundEmitter;
import dev.theatricalmod.theatrical.network.SendAudioDataPacket;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResource;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

    public TileEntitySpeaker() {
        super(TheatricalTiles.SPEAKER.get());
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
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
        int audioID = new Random().nextInt(100);
        SimpleSound sound = new SimpleSound(SoundEvents.EVENT_RAID_HORN, SoundCategory.RECORDS, 1.0f, 1.0f, this.getPos());
        SoundEventAccessor soundeventaccessor = sound.createAccessor(Minecraft.getInstance().getSoundHandler());
        CompletableFuture.supplyAsync(() -> {
            try (
                    IResource iresource = Minecraft.getInstance().getResourceManager().getResource(sound.getSound().getSoundAsOggLocation());
                    InputStream inputstream = iresource.getInputStream();
                    OggAudioStream oggaudiostream = new OggAudioStream(inputstream);
            ) {
                ByteBuffer bytebuffer = oggaudiostream.readOggSound();
                TheatricalNetworkHandler.MAIN.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 10d, world.getDimensionKey())), new SendAudioDataPacket(audioID, oggaudiostream.getAudioFormat().getSampleRate(), oggaudiostream.getAudioFormat().getChannels(), bytebuffer));
                return null;
            } catch (IOException ioexception) {
                throw new CompletionException(ioexception);
            }
        }, Util.getServerExecutor());
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

}
