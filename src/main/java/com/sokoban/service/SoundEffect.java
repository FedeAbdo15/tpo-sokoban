package com.sokoban.service;

/** Efectos de sonido del juego. El nombre en minuscula es el del archivo .wav. */
public enum SoundEffect {
    MOVE,
    PUSH,
    STORE,
    BREAK,
    DOOR_OPEN,
    TELEPORT,
    UNDO,
    WIN,
    LOSE;

    public String fileName() {
        return name().toLowerCase() + ".wav";
    }
}
