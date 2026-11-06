package com.sokoban.model.game.loss;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import com.sokoban.model.board.MoveResult;
import com.sokoban.model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LossConditionTest {

    @Test
    @DisplayName("1. Deadlock: una caja en una esquina que no es destino")
    void boxInCornerIsDeadlock() {
        Level level = TestLevels.level(
                "#####",
                "#   #",
                "#$  #",
                "#@ .#",
                "#####");

        assertEquals(MoveResult.OK, level.move(Direction.UP));
        assertEquals(LossReason.DEADLOCK, level.getLossReason());
    }

    @Test
    @DisplayName("2. Deadlock: un bloque de 2x2 formado por cajas o paredes")
    void twoByTwoBlockIsDeadlock() {
        Level level = TestLevels.level(
                "#######",
                "#  $  #",
                "#   $ #",
                "#   @ #",
                "#..   #",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.UP));
        assertEquals(LossReason.DEADLOCK, level.getLossReason());
    }

    @Test
    @DisplayName("3. Una caja en una esquina que si es su destino no es deadlock (se chequea antes la victoria)")
    void boxInCornerOnItsTargetIsNotDeadlock() {
        Level level = TestLevels.level(
                "#####",
                "#.  #",
                "#$  #",
                "#@  #",
                "#####");

        assertEquals(MoveResult.OK, level.move(Direction.UP));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("4. El deadlock se evalua solo sobre la caja empujada y sus vecinas")
    void deadlockOnlyChecksPushedBox() {
        // The box at (1,1) starts in a corner, but it is not the one being pushed.
        Level level = TestLevels.level(
                "#######",
                "#$   .#",
                "#     #",
                "#@$  .#",
                "#     #",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("5. Tiempo en 0 da derrota (TimeOutCondition) - decision pendiente a confirmar")
    void timeOutLoses() {
        Level level = TestLevels.level(
                "######",
                "#@$ .#",
                "######");
        TimeOutCondition condition = new TimeOutCondition();
        assertFalse(condition.isMet(level));

        level.elapseSeconds(level.getTimeLimitSeconds());

        assertEquals(0, level.getRemainingSeconds());
        assertTrue(condition.isMet(level));
        assertEquals(LossReason.TIME_OUT, level.getLossReason());
    }

    @Test
    @DisplayName("6. Caja rota da derrota (BrokenBoxCondition)")
    void brokenBoxLoses() {
        Level level = TestLevels.level(
                "#######",
                "#@1  :#",
                "#######");

        level.move(Direction.RIGHT); // uses its only push

        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));
        assertEquals(LossReason.BROKEN_BOX, level.getLossReason());
        assertTrue(new BrokenBoxCondition().isMet(level));
    }

    @Test
    @DisplayName("7. Cada derrota devuelve el LossReason que le corresponde")
    void eachConditionHasItsReason() {
        assertEquals(LossReason.DEADLOCK, new DeadlockCondition().getReason());
        assertEquals(LossReason.TIME_OUT, new TimeOutCondition().getReason());
        assertEquals(LossReason.BROKEN_BOX, new BrokenBoxCondition().getReason());
        assertEquals(LossReason.WRONG_STORAGE, new WrongStorageCondition().getReason());
        assertEquals(LossReason.EARLY_KEY_STORAGE, new EarlyKeyStorageCondition().getReason());
    }
}
