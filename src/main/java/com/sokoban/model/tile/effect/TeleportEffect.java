package com.sokoban.model.tile.effect;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.Occupant;

/**
 * Portal: el ocupante aparece en el portal pareja.
 *
 * <p>La salida la fija {@code LevelBuilder} al armar el nivel, que es el unico que
 * conoce todas las posiciones. De esa forma el efecto no necesita recorrer el tablero
 * buscando su pareja ni preguntar por el tipo concreto de baldosa.
 */
public class TeleportEffect implements EntryEffect {
    private Position exit;

    public void setExit(Position exit) {
        this.exit = exit;
    }

    public Position getExit() {
        return exit;
    }

    @Override
    public Position resolve(Board board, Occupant occupant, Position landing, Direction direction) {
        if (exit == null) {
            return landing;
        }
        return board.admits(exit, occupant) ? exit : null;
    }
}
