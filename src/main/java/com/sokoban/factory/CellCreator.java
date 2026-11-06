package com.sokoban.factory;

import com.sokoban.model.board.Position;

/** Factory Method: each implementation decides which tile and occupant a character creates. */
public interface CellCreator {
    CellContent create(Position p, char symbol);
}
