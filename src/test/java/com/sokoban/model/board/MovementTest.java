package com.sokoban.model.board;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class MovementTest {

    private static Level openRoom() {
        return TestLevels.level(
                "#######",
                "#     #",
                "#  @  #",
                "#     #",
                "#  $. #",
                "#######");
    }

    @Test
    @DisplayName("1. El jugador se mueve 1 celda en las 4 direcciones si el destino es piso")
    void movesOneCellInEachDirection() {
        Position start = new Position(2, 3);

        for (Direction direction : Direction.values()) {
            Level level = openRoom();

            assertEquals(MoveResult.OK, level.move(direction), direction.name());
            assertEquals(start.move(direction), level.getBoard().getPlayerPosition(), direction.name());
        }
    }

    @Test
    @DisplayName("2. El jugador no se mueve contra una pared")
    void doesNotMoveIntoWall() {
        Level level = TestLevels.level(
                "######",
                "#@$. #",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.UP));
        assertEquals(MoveResult.BLOCKED, level.move(Direction.LEFT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. El jugador no se mueve contra terreno no accesible (%)")
    void doesNotMoveIntoVoid() {
        Level level = TestLevels.level(
                "######",
                "#@%  #",
                "# $. #",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("3. No existe movimiento en diagonal")
    void thereAreNoDiagonalDirections() {
        assertEquals(EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT),
                EnumSet.allOf(Direction.class));
        for (Direction direction : Direction.values()) {
            int distance = Math.abs(direction.getRowOffset()) + Math.abs(direction.getColOffset());
            assertEquals(1, distance, direction.name());
        }
    }

    @Test
    @DisplayName("4. El jugador no puede pisar un punto de guardado vacio (es un agujero)")
    void cannotStepOnEmptyStoragePoint() {
        Level level = TestLevels.level(
                "######",
                "#@.  #",
                "#  $ #",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("5. El jugador puede pasar sobre un punto de guardado con una caja encima (puente)")
    void canWalkOverStoredBox() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#   $.#",
                "#######");

        level.move(Direction.RIGHT); // stores the box at (1,3)
        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)), "the stored box stays as a bridge");
    }

    @Test
    @DisplayName("6. Moverse sin empujar suma un movimiento y no suma empuje")
    void walkingCountsMoveButNotPush() {
        Level level = openRoom();

        level.move(Direction.UP);

        assertEquals(1, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }
}
