package com.sokoban.model.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    @DisplayName("1. Mover una posicion avanza 1 celda en cada una de las 4 direcciones")
    void movesOneCellInEachDirection() {
        Position start = new Position(2, 3);

        assertEquals(new Position(1, 3), start.move(Direction.UP));
        assertEquals(new Position(3, 3), start.move(Direction.DOWN));
        assertEquals(new Position(2, 2), start.move(Direction.LEFT));
        assertEquals(new Position(2, 4), start.move(Direction.RIGHT));
    }

    @Test
    @DisplayName("2. Mover no modifica la posicion original (es inmutable)")
    void moveDoesNotChangeOriginal() {
        Position start = new Position(2, 3);

        start.move(Direction.RIGHT);

        assertEquals(2, start.row());
        assertEquals(3, start.col());
    }

    @Test
    @DisplayName("3. Ir y volver en la direccion contraria vuelve a la misma posicion")
    void oppositeDirectionsCancelOut() {
        Position start = new Position(4, 4);

        assertEquals(start, start.move(Direction.UP).move(Direction.DOWN));
        assertEquals(start, start.move(Direction.LEFT).move(Direction.RIGHT));
    }

    @Test
    @DisplayName("4. Dos posiciones con la misma fila y columna son iguales")
    void equalByRowAndColumn() {
        assertEquals(new Position(1, 5), new Position(1, 5));
        assertEquals(new Position(1, 5).hashCode(), new Position(1, 5).hashCode());
        assertNotEquals(new Position(1, 5), new Position(5, 1));
    }
}
