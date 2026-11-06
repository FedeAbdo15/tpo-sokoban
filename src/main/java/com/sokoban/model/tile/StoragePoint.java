package com.sokoban.model.tile;

import com.sokoban.model.box.Box;
import com.sokoban.model.box.BoxType;

/**
 * Casilla de destino. Un destino vacio es un hueco: el jugador no puede pisarlo.
 * Cuando recibe la caja correcta se la queda, y entonces se puede caminar por encima
 * (la caja guardada hace de puente y ya no se puede volver a empujar).
 */
public class StoragePoint extends Tile {
    private final BoxType type;
    private Box storedBox;

    public StoragePoint(BoxType type) {
        this.type = type;
    }

    public BoxType getType() {
        return type;
    }

    /** Le pregunta a la caja si puede guardarse aca (Template Method del lado de la caja). */
    public StorageResult receive(Box box) {
        return box.storeAt(this);
    }

    public boolean isFilled() {
        return storedBox != null;
    }

    /** Pone o saca la caja guardada sin pasar por las reglas. Lo usa el undo. */
    @Override
    public void setStoredBox(Box box) {
        this.storedBox = box;
    }

    @Override
    public Box getStoredBox() {
        return storedBox;
    }

    @Override
    public boolean isPendingStorage() {
        return storedBox == null;
    }

    @Override
    public boolean allowsPlayer() {
        return isFilled();
    }

    @Override
    public boolean allowsBox(Box box) {
        return !isFilled();
    }

    @Override
    public LandingOutcome receiveBox(Box box) {
        switch (receive(box)) {
            case STORED:
                this.storedBox = box;
                return LandingOutcome.STORED;
            case WRONG_BOX:
                return LandingOutcome.WRONG_STORAGE;
            default:
                return LandingOutcome.KEY_NOT_USED;
        }
    }
}
