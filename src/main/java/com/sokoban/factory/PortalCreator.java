package com.sokoban.factory;

import com.sokoban.model.board.Position;
import com.sokoban.model.tile.Portal;

/** 'P'..'S': portal; each letter appears twice to form a pair. */
public class PortalCreator implements CellCreator {

    @Override
    public CellContent create(Position p, char symbol) {
        return new CellContent(new Portal(symbol), null);
    }
}
