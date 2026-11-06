package com.sokoban.model.tile.mechanism;

import com.sokoban.model.box.Box;
import com.sokoban.model.tile.Tile;

public class Door extends Tile {
    private final char group;
    private boolean open;

    public Door(char group) {
        this.group = Character.toUpperCase(group);
    }

    public char getGroup() {
        return group;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }

    /** Un muro cerrado tapa lo que hay detras; abierto, deja ver. */
    @Override
    public boolean isTransparent() {
        return open;
    }

    @Override
    public boolean allowsPlayer() {
        return open;
    }

    @Override
    public boolean allowsBox(Box box) {
        return open;
    }
}
