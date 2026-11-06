package com.sokoban.model.box;

import com.sokoban.model.board.Direction;
import com.sokoban.model.tile.Tile;

public class Player implements Occupant {
    private Direction facing;

    public Player(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return facing;
    }

    /** La vista dibuja al Sokoban segun hacia donde mira. */
    public void face(Direction direction) {
        this.facing = direction;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canEnter(Tile tile) {
        return tile.allowsPlayer();
    }
}
