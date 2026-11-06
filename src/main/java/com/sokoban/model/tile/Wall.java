package com.sokoban.model.tile;

import com.sokoban.model.box.Box;

public class Wall extends Tile {

    @Override
    public boolean isTransparent() {
        return false;
    }

    @Override
    public boolean allowsPlayer() {
        return false;
    }

    @Override
    public boolean allowsBox(Box box) {
        return false;
    }
}
