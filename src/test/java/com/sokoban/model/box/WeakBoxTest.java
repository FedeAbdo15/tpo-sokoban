package com.sokoban.model.box;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.board.Position;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeakBoxTest {

    @Test
    @DisplayName("1. WeakBox(1) aguanta 1 empuje: el primero da OK y el segundo da BROKEN y la caja desaparece")
    void weakBoxOfOneBreaksOnSecondPush() {
        Level level = TestLevels.level(
                "#######",
                "#@1  :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(0, box.getRemainingPushes());

        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)), "the board removes the broken box");
        assertNull(level.getBoard().getBoxAt(new Position(1, 4)), "the broken box does not move on");
    }

    @Test
    @DisplayName("2. WeakBox(3): los empujes intermedios dan OK y el contador baja de a 1")
    void weakBoxOfThreeCountsDownOneByOne() {
        Level level = TestLevels.level(
                "#########",
                "#@3    :#",
                "#########");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        for (int expected = 2; expected >= 0; expected--) {
            assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
            assertEquals(expected, box.getRemainingPushes());
        }
        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));
    }

    @Test
    @DisplayName("3. Un empuje bloqueado da BLOCKED y no descuenta")
    void blockedPushDoesNotCountDown() {
        Level level = TestLevels.level(
                "#######",
                "#@2# :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(2, box.getRemainingPushes());
        assertSame(box, level.getBoard().getBoxAt(new Position(1, 2)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("4. Caminar al lado de la WeakBox sin empujarla no descuenta")
    void walkingNextToBoxDoesNotCountDown() {
        Level level = TestLevels.level(
                "#######",
                "#@    #",
                "#  3 :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(2, 3));

        for (int i = 0; i < 4; i++) {
            level.move(Direction.RIGHT);
        }

        assertEquals(3, box.getRemainingPushes());
        assertEquals(0, level.getStats().getPushes());
    }
}
