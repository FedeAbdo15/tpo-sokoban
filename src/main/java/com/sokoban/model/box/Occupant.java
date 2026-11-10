package com.sokoban.model.box;

import com.sokoban.model.tile.Tile;

public interface Occupant {

    boolean isPushable();

    /**
     * Polimorfismo (GRASP): cada ocupante le pregunta a la baldosa lo que le corresponde.
     * Evita que el tablero tenga que preguntar "soy caja o soy jugador".
     */
    boolean canEnter(Tile tile);
}
