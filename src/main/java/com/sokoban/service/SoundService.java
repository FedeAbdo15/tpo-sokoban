package com.sokoban.service;

import com.sokoban.model.game.event.GameEvent;
import com.sokoban.model.game.event.GameListener;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Reproduce un efecto por cada evento del juego.
 *
 * <p>Es un {@link GameListener}: el modelo no sabe que existe el sonido, solo publica
 * lo que paso. Los clips se precargan una vez y se comparten, con el mismo criterio
 * Flyweight que los sprites.
 *
 * <p>Si la maquina no tiene placa de sonido disponible, el juego sigue funcionando
 * en silencio en lugar de romperse.
 */
public class SoundService implements GameListener {

    private static final String BASE_PATH = "/assets/audio/";

    private final Map<SoundEffect, Clip> clips = new EnumMap<>(SoundEffect.class);
    private boolean enabled = true;

    public SoundService() {
        for (SoundEffect effect : SoundEffect.values()) {
            load(effect).ifPresent(clip -> clips.put(effect, clip));
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case PLAYER_MOVED -> play(SoundEffect.MOVE);
            case BOX_PUSHED -> play(SoundEffect.PUSH);
            case DOOR_OPENED -> play(SoundEffect.DOOR_OPEN);
            case UNDO -> play(SoundEffect.UNDO);
            case LEVEL_WON -> play(SoundEffect.WIN);
            case LEVEL_LOST -> play(SoundEffect.LOSE);
        }
    }

    public void play(SoundEffect effect) {
        if (!enabled) {
            return;
        }
        Clip clip = clips.get(effect);
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    private java.util.Optional<Clip> load(SoundEffect effect) {
        try (InputStream raw = SoundService.class.getResourceAsStream(BASE_PATH + effect.fileName())) {
            if (raw == null) {
                return java.util.Optional.empty();
            }
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(raw))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                return java.util.Optional.of(clip);
            }
        } catch (Exception e) {
            return java.util.Optional.empty(); // sin audio disponible: el juego sigue mudo
        }
    }
}
