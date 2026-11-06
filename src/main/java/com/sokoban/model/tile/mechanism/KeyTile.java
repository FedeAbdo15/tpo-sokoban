package com.sokoban.model.tile.mechanism;

import com.sokoban.model.box.Box;
import com.sokoban.model.tile.LandingOutcome;
import com.sokoban.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Casillero cerrojo. Es el Subject del Observer: no conoce a los muros, solo avisa que
 * se activo su grupo. Quien escucha es el {@link DoorGroup} de esa letra.
 */
public class KeyTile extends Tile {
    private final char group;
    private final List<KeyTileListener> listeners = new ArrayList<>();

    public KeyTile(char group) {
        this.group = Character.toUpperCase(group);
    }

    /** Letra del grupo en mayuscula: el casillero 'a' abre los muros 'A'. */
    public char getGroup() {
        return group;
    }

    public void addListener(KeyTileListener listener) {
        listeners.add(listener);
    }

    @Override
    public LandingOutcome receiveBox(Box box) {
        if (!box.opensDoors()) {
            return LandingOutcome.NONE;
        }
        box.markDoorOpened();
        for (KeyTileListener listener : listeners) {
            listener.onKeyTileActivated(group);
        }
        return LandingOutcome.DOOR_OPENED;
    }
}
