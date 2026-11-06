package com.sokoban.model.tile.effect;

import com.sokoban.model.board.Board;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.Occupant;

/**
 * Hielo: el ocupante sigue avanzando en la misma direccion.
 *
 * <p>No hay bucle: se delega en el efecto de la celda siguiente. Si esa celda es piso,
 * {@link NoEffect} devuelve la posicion y la cadena termina; si vuelve a ser hielo, sigue.
 * Asi el deslizamiento frena en la primera baldosa que no resbala, o justo antes del obstaculo.
 */
public class SlideEffect implements EntryEffect {

    @Override
    public Position resolve(Board board, Occupant occupant, Position landing, Direction direction) {
        Position next = landing.move(direction);
        if (!board.admits(next, occupant)) {
            return landing;
        }
        return board.getTile(next).getEntryEffect().resolve(board, occupant, next, direction);
    }
}
