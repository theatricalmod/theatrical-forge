package dev.theatricalmod.theatrical.tiles.sound;

import dev.theatricalmod.theatrical.api.sound.ISoundEmitter;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;

import java.util.HashMap;

public class TileEntityAmplifier extends TileEntityTheatricalBase {

    // volume = (db - 40) / 10
    // power per tick = volume

    /**
     * Amplification in dB
     */
    private float amplification;

    public TileEntityAmplifier() {
        super(TheatricalTiles.AMPLIFIER.get());
    }

    public void playSound(SoundEvent sound, float volume) {

    }

    private void playSound(ISoundEmitter soundEmitter, SoundEvent sound, float volume) {
        float amplifiedVolume = volume * amplification;
        soundEmitter.playSound(sound, amplifiedVolume);
    }

    public float getAmplification() {
        return amplification;
    }

    public void setAmplification(float amplification) {
        this.amplification = Math.min(Math.max(-40f, amplification), 10f);
    }
}
