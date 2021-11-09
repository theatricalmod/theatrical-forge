package dev.theatricalmod.theatrical.api.sound;


import net.minecraft.util.SoundEvent;

public interface ISoundEmitter {

    /**
     * Play a sound from the speaker
     * @param sound -> SoundEvent
     * @param volume -> Amplified volume in dB
     */
    void playSound(SoundEvent sound, float volume);

}
