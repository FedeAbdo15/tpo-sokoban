package com.sokoban.model.history;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.WeakBox;
import com.sokoban.model.game.Level;
import com.sokoban.model.tile.mechanism.Door;
import com.sokoban.model.tile.mechanism.KeyTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regla de la consigna:
 *
 * <blockquote>
 * "Se permite deshacer los ultimos 15 movimientos realizados por el jugador. Para ello,
 * debe existir un boton undo que, cada vez que se presione, restaure el estado del nivel
 * 5 movimientos hacia atras, hasta un maximo de tres usos consecutivos."
 * </blockquote>
 *
 * De ahi salen las tres constantes de abajo: el historial guarda los ultimos 15 estados
 * (= 5 x 3), cada uso del boton retrocede 5 movimientos y se permiten 3 usos seguidos.
 * El contador de usos es *consecutivo*: al volver a mover, se reinicia en 3.
 * Si en el historial hay menos de 5 movimientos, el undo vuelve al estado mas viejo
 * que tenga guardado.
 */
class UndoTest {

    /** Movimientos que retrocede cada vez que se presiona el boton. */
    private static final int STEPS_PER_UNDO = 5;

    /** Usos consecutivos permitidos antes de tener que volver a mover. */
    private static final int MAX_CONSECUTIVE_UNDOS = 3;

    /** Movimientos que conserva el historial: 5 x 3. */
    private static final int HISTORY_SIZE = STEPS_PER_UNDO * MAX_CONSECUTIVE_UNDOS;

    private static final int HALL_WIDTH = 28;

    /** Sala larga para caminar sin empujar: el jugador arranca en (1,1) y puede llegar a (1,26). */
    private static Level hall() {
        return TestLevels.level(
                "#".repeat(HALL_WIDTH),
                "#@" + " ".repeat(HALL_WIDTH - 3) + "#",
                "#  $" + " ".repeat(HALL_WIDTH - 8) + ".  #",
                "#".repeat(HALL_WIDTH));
    }

    /** Pasillo con una caja normal: 5 empujes la dejan en (1,7), todavia sin guardar. */
    private static Level corridor() {
        return TestLevels.level(
                "##########",
                "#@$     .#",
                "##########");
    }

    /** Pasillo con una caja fragil que aguanta 6 empujes. */
    private static Level weakCorridor() {
        return TestLevels.level(
                "##########",
                "#@6     :#",
                "##########");
    }

    /** Sala con caja llave, casillero cerrojo 'a' y muro 'A' que oculta la parte de abajo. */
    private static Level keyRoom() {
        return TestLevels.level(
                "########",
                "#@K a  #",
                "####A###",
                "#  ;   #",
                "########");
    }

    private static void walkRight(Level level, int times) {
        for (int i = 0; i < times; i++) {
            assertEquals(MoveResult.OK, level.move(Direction.RIGHT), "paso " + (i + 1));
        }
    }

    @Test
    @DisplayName("1. El undo restaura la posicion del jugador y de las cajas")
    void restoresPlayerAndBoxes() {
        Level level = corridor();
        walkRight(level, STEPS_PER_UNDO); // jugador (1,6), caja (1,7)

        assertTrue(level.undo());

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)), "la caja vuelve a su lugar");
        assertNull(level.getBoard().getBoxAt(new Position(1, 7)));
    }

    @Test
    @DisplayName("2. El undo restaura el contador de la caja fragil")
    void restoresWeakBoxCounter() {
        Level level = weakCorridor();
        walkRight(level, STEPS_PER_UNDO); // 5 de los 6 empujes

        level.undo();

        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));
        assertNotNull(box, "la caja fragil vuelve a su posicion inicial");
        assertEquals(6, box.getRemainingPushes());
    }

    @Test
    @DisplayName("3. El undo restaura muros, baldosas cerrojo y visibilidad")
    void restoresDoorsTilesAndVisibility() {
        Level level = keyRoom();
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT); // la caja llave cae en 'a' y abre el muro 'A'

        level.undo();

        Door door = (Door) level.getBoard().getTile(new Position(2, 4));
        assertFalse(door.isOpen(), "el muro vuelve a estar cerrado");
        assertInstanceOf(KeyTile.class, level.getBoard().getTile(new Position(1, 4)),
                "la baldosa cerrojo vuelve a aparecer");
        assertFalse(level.getVisibilityMap().isVisible(new Position(3, 1)),
                "la zona de abajo vuelve a quedar oculta");
    }

    @Test
    @DisplayName("4. Con menos de 5 movimientos en el historial, el undo vuelve al inicio")
    void undoWithFewerThanFiveMovesReturnsToStart() {
        Level level = hall();
        walkRight(level, 3);

        assertTrue(level.undo());

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("5. Cada uso del boton retrocede exactamente 5 movimientos")
    void eachUndoRewindsFiveMoves() {
        Level level = hall();
        walkRight(level, HISTORY_SIZE); // 15 pasos: (1,1) -> (1,16)
        assertEquals(new Position(1, 16), level.getBoard().getPlayerPosition());

        assertTrue(level.undo());
        assertEquals(new Position(1, 11), level.getBoard().getPlayerPosition());

        assertTrue(level.undo());
        assertEquals(new Position(1, 6), level.getBoard().getPlayerPosition());

        assertTrue(level.undo());
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("6. Solo se permiten 3 usos seguidos: el cuarto no hace nada")
    void onlyThreeConsecutiveUndos() {
        Level level = hall();
        walkRight(level, HISTORY_SIZE);

        for (int use = 1; use <= MAX_CONSECUTIVE_UNDOS; use++) {
            assertTrue(level.undo(), "uso " + use);
            assertEquals(MAX_CONSECUTIVE_UNDOS - use, level.getRemainingUndos());
        }

        assertFalse(level.undo(), "el cuarto uso seguido no se aplica");
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getRemainingUndos());
        assertEquals(MAX_CONSECUTIVE_UNDOS, level.getStats().getUndos(), "el cuarto no se cuenta");
    }

    @Test
    @DisplayName("7. Al volver a mover se reinicia el contador de usos consecutivos")
    void undosRefillAfterMoving() {
        Level level = hall();
        walkRight(level, HISTORY_SIZE);
        for (int use = 0; use < MAX_CONSECUTIVE_UNDOS; use++) {
            level.undo();
        }
        assertEquals(0, level.getRemainingUndos());

        walkRight(level, 1);

        assertEquals(MAX_CONSECUTIVE_UNDOS, level.getRemainingUndos(), "mover habilita el boton de nuevo");
        assertTrue(level.undo());
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("8. El historial conserva solo los ultimos 15 movimientos")
    void historyKeepsTheLastFifteenMoves() {
        Level level = hall();
        walkRight(level, 20); // (1,1) -> (1,21)

        for (int use = 0; use < MAX_CONSECUTIVE_UNDOS; use++) {
            assertTrue(level.undo(), "uso " + (use + 1));
        }

        assertEquals(new Position(1, 6), level.getBoard().getPlayerPosition(),
                "se deshacen 15 de los 20 movimientos, no se puede volver al inicio");
    }

    @Test
    @DisplayName("9. Cada undo suma 1 a LevelStats.undos")
    void eachUndoIsCounted() {
        Level level = hall();
        walkRight(level, HISTORY_SIZE);

        level.undo();
        assertEquals(1, level.getStats().getUndos());
        level.undo();
        assertEquals(2, level.getStats().getUndos());
    }

    @Test
    @DisplayName("10. Con el historial vacio, el undo no hace nada")
    void undoWithEmptyHistoryDoesNothing() {
        Level level = hall();

        assertFalse(level.undo());

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getStats().getUndos());
        assertEquals(MAX_CONSECUTIVE_UNDOS, level.getRemainingUndos());
    }
}
