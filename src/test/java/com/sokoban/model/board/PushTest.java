package com.sokoban.model.board;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.box.NormalBox;
import com.sokoban.model.box.PushResult;
import com.sokoban.model.box.WeakBox;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PushTest {

    @Test
    @DisplayName("1. Empujar una caja hacia piso libre mueve la caja y al jugador, y suma 1 empuje")
    void pushMovesBoxAndPlayer() {
        Level level = TestLevels.level(
                "#######",
                "#@$  .#",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("2. No se puede empujar una caja contra una pared")
    void cannotPushBoxIntoWall() {
        Level level = TestLevels.level(
                "######",
                "#@$#.#",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("3. No se pueden empujar dos cajas en fila: BLOCKED y nada se mueve")
    void cannotPushTwoBoxes() {
        Level level = TestLevels.level(
                "########",
                "#@$$ ..#",
                "########");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 4)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("4. NormalBox no tiene efecto extra al ser empujada")
    void normalBoxHasNoExtraEffect() {
        NormalBox box = new NormalBox();

        for (int i = 0; i < 20; i++) {
            assertEquals(PushResult.PUSHED, box.push());
        }
    }

    @Test
    @DisplayName("5. WeakBox con 3 empujes: despues de 2 empujes le queda 1")
    void weakBoxCountsDown() {
        WeakBox box = new WeakBox(3);

        box.push();
        box.push();

        assertEquals(1, box.getRemainingPushes());
    }

    @Test
    @DisplayName("6. A WeakBox le baja el contador solo cuando la empujan, no cuando el jugador camina")
    void weakBoxOnlyCountsPushes() {
        Level level = TestLevels.level(
                "#######",
                "#  3 :#",
                "#@    #",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 3));

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("7. Cuando a la WeakBox se le terminan los empujes, el resultado es BROKEN")
    void weakBoxBreaksWhenOutOfPushes() {
        // "Aguanta N empujes": the first N pushes are fine, the next one breaks it.
        WeakBox box = new WeakBox(2);

        assertEquals(PushResult.PUSHED, box.push());
        assertEquals(PushResult.PUSHED, box.push());
        assertEquals(PushResult.BROKEN, box.push());
    }
}
