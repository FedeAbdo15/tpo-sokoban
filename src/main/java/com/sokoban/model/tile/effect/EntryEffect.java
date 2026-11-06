package com.sokoban.model.tile.effect;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.Occupant;

/**
 * Strategy: que le pasa a quien termina de entrar en una baldosa.
 *
 * <p>El efecto es del terreno y no de la caja, por eso vive en {@link com.sokoban.model.tile.Tile}
 * y no en {@link com.sokoban.model.box.Box}. El tablero lo aplica una sola vez, despues de
 * posicionar al ocupante, sin saber de que tipo de baldosa se trata.
 */
public interface EntryEffect {

    /**
     * @param landing celda en la que el ocupante acaba de entrar
     * @return la posicion final del ocupante, o {@code null} si el movimiento debe bloquearse
     */
    Position resolve(Board board, Occupant occupant, Position landing, Direction direction);
}
