package com.sokoban.model.tile;

import com.sokoban.model.board.Position;
import com.sokoban.model.box.Box;
import com.sokoban.model.tile.effect.EntryEffect;
import com.sokoban.model.tile.effect.NoEffect;

/**
 * Polymorphism (GRASP): cada baldosa responde que deja entrar y que provoca,
 * asi el tablero nunca pregunta por el tipo concreto de baldosa.
 */
public abstract class Tile {

    private static final EntryEffect NO_EFFECT = new NoEffect();

    public boolean allowsPlayer() {
        return true;
    }

    public boolean allowsBox(Box box) {
        return true;
    }

    /** Strategy: que le pasa a quien termina de entrar aca. Por defecto, nada. */
    public EntryEffect getEntryEffect() {
        return NO_EFFECT;
    }

    /**
     * Que provoca una caja que termina su movimiento aca. La baldosa actualiza su propio
     * estado y le informa al tablero la consecuencia, para que el tablero aplique lo que
     * la baldosa no puede hacer sola (sacar la caja de la celda o reemplazar la baldosa).
     */
    public LandingOutcome receiveBox(Box box) {
        return LandingOutcome.NONE;
    }

    /** Es un destino que todavia espera su caja. Define la condicion de victoria. */
    public boolean isPendingStorage() {
        return false;
    }

    /** La caja que esta baldosa tiene guardada, o null si no guarda ninguna. */
    public Box getStoredBox() {
        return null;
    }

    /** Guarda una caja sin pasar por las reglas. Solo lo usa el undo al restaurar. */
    public void setStoredBox(Box box) {
    }

    /**
     * Si deja ver, y por lo tanto recorrer, lo que hay del otro lado.
     * Lo usa el mapa de visibilidad. Las cajas no tapan: tapan las paredes y los muros cerrados.
     */
    public boolean isTransparent() {
        return true;
    }

    /** Posicion con la que esta baldosa conecta a distancia (portales), o null. */
    public Position getLinkedPosition() {
        return null;
    }
}
