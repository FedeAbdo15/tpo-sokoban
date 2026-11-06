package com.sokoban.model.game.score;

import com.sokoban.model.game.LevelStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {

    private static final int TARGET = 1000;

    private final ScoreCalculator calculator = new StandardScoreCalculator();

    private static LevelStats stats(int pushes, int undos) {
        LevelStats stats = new LevelStats();
        for (int i = 0; i < pushes; i++) {
            stats.addPush();
        }
        for (int i = 0; i < undos; i++) {
            stats.addUndo();
        }
        return stats;
    }

    /** A score made only of remaining seconds, to hit an exact value. */
    private int starsFor(int score) {
        return calculator.calculate(stats(0, 0), score, 0, 0, TARGET).getStars();
    }

    @Test
    @DisplayName("1. Suma: +1 por segundo restante, +100 por caja guardada y +100 por puerta abierta")
    void addsBonuses() {
        assertEquals(30, calculator.calculate(stats(0, 0), 30, 0, 0, TARGET).getScore());
        assertEquals(200, calculator.calculate(stats(0, 0), 0, 2, 0, TARGET).getScore());
        assertEquals(300, calculator.calculate(stats(0, 0), 0, 0, 3, TARGET).getScore());
    }

    @Test
    @DisplayName("2. Resta: -1 por empuje y -250 por cada undo usado")
    void subtractsPenalties() {
        assertEquals(500 - 10, calculator.calculate(stats(10, 0), 500, 0, 0, TARGET).getScore());
        assertEquals(500 - 250, calculator.calculate(stats(0, 1), 500, 0, 0, TARGET).getScore());
    }

    @Test
    @DisplayName("3. Un caso completo con numeros concretos da el total esperado")
    void fullExample() {
        // 60 s + 3 boxes + 1 door - 20 pushes - 1 undo = 60 + 300 + 100 - 20 - 250 = 190
        ScoreResult result = calculator.calculate(stats(20, 1), 60, 3, 1, TARGET);

        assertEquals(190, result.getScore());
    }

    @Test
    @DisplayName("4. Estrellas: <=50% da 1, <=85% da 2, >85% da 3")
    void starsByPercentage() {
        assertEquals(1, starsFor(100));
        assertEquals(2, starsFor(700));
        assertEquals(3, starsFor(950));
    }

    @Test
    @DisplayName("5. Los limites exactos: 50% da 1 estrella y 85% da 2")
    void exactLimits() {
        assertEquals(1, starsFor(500));
        assertEquals(2, starsFor(501));
        assertEquals(2, starsFor(850));
        assertEquals(3, starsFor(851));
    }
}
