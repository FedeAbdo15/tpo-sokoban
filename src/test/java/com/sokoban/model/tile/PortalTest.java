package com.sokoban.model.tile;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.board.Position;
import com.sokoban.model.box.NormalBox;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortalTest {

    @Test
    @DisplayName("1. El jugador que entra en el portal P aparece en el otro extremo P")
    void playerTeleports() {
        Level level = TestLevels.level(
                "#########",
                "#@P   P #",
                "#  $.   #",
                "#########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 6), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. Una caja empujada a un portal aparece en el otro extremo")
    void boxTeleports() {
        Level level = TestLevels.level(
                "##########",
                "#@$P   P #",
                "#     .  #",
                "##########");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 7)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("3. Los portales funcionan en las dos direcciones")
    void portalsWorkBothWays() {
        Level level = TestLevels.level(
                "#########",
                "#@P   P #",
                "#  $.   #",
                "#########");

        level.move(Direction.RIGHT); // (1,2) -> (1,6)
        level.move(Direction.RIGHT); // (1,7)
        level.move(Direction.LEFT);  // enters (1,6) -> (1,2)

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("4. Si hay varios pares (P, Q...), cada uno solo lleva a su pareja")
    void eachPortalLeadsToItsPair() {
        Level level = TestLevels.level(
                "##########",
                "#@PQ   QP#",
                "#  $.    #",
                "##########");

        level.move(Direction.RIGHT);
        assertEquals(new Position(1, 8), level.getBoard().getPlayerPosition(), "P leads to the other P");

        level.move(Direction.LEFT);
        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition(), "Q leads to the other Q");
    }

    @Test
    @DisplayName("5. Si la salida del portal esta ocupada, el movimiento se bloquea (regla a confirmar)")
    void occupiedExitBlocksMovement() {
        Level level = TestLevels.level(
                "#########",
                "#@P   P #",
                "#  $.   #",
                "#########");
        level.getBoard().placeBox(new Position(1, 6), new NormalBox());

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }
}
