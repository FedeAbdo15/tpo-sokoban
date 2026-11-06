package com.sokoban.factory;

import com.sokoban.model.board.Position;
import com.sokoban.model.tile.mechanism.Door;

/** 'A'..'E': closed door of that group. */
public class DoorCreator implements CellCreator {

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new Door(symbol), null);
    }
}
