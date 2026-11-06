package com.sokoban.model.game;

import com.sokoban.factory.TestLevels;
import com.sokoban.model.board.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelStatsTest {

    @Test
    @DisplayName("1. Los contadores arrancan en 0 y suman de a uno")
    void countersStartAtZeroAndIncrement() {
        LevelStats stats = new LevelStats();
        assertEquals(0, stats.getMoves());
        assertEquals(0, stats.getPushes());
        assertEquals(0, stats.getUndos());

        stats.addMove();
        stats.addMove();
        stats.addPush();
        stats.addUndo();

        assertEquals(2, stats.getMoves());
        assertEquals(1, stats.getPushes());
        assertEquals(1, stats.getUndos());
    }

    @Test
    @DisplayName("1. Cuenta bien movimientos, empujes y undos durante una partida")
    void countsDuringPlay() {
        Level level = TestLevels.level(
                "########",
                "#@     #",
                "#  $  .#",
                "#      #",
                "########");

        level.move(Direction.RIGHT); // walk
        level.move(Direction.RIGHT); // walk
        level.move(Direction.DOWN);  // push the box down
        level.move(Direction.UP);    // walk
        level.undo();

        assertEquals(1, level.getStats().getPushes());
        assertEquals(1, level.getStats().getUndos());
        assertTrue(level.getStats().getMoves() >= 3, "at least the three plain steps are counted");
    }
}
