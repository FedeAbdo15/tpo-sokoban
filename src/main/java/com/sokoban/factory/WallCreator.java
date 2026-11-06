package com.sokoban.factory;

import com.sokoban.model.board.Position;
import com.sokoban.model.tile.Wall;

public class WallCreator implements CellCreator {

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new Wall(), null);
    }
}
