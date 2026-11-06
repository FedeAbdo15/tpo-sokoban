package com.sokoban.model.tile.effect;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.Occupant;

/** Baldosa comun: el ocupante se queda donde entro. Corta la recursion del deslizamiento. */
public class NoEffect implements EntryEffect {

    @Override
    public Position resolve(Board board, Occupant occupant, Position landing, Direction direction) {
        return landing;
    }
}
