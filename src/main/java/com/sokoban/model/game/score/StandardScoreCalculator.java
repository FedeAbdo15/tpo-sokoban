package com.sokoban.model.game.score;

import com.sokoban.model.game.LevelStats;

/**
 * Criterio de puntaje definido por el equipo:
 * +1 por segundo restante, +100 por caja guardada, +100 por muro abierto,
 * -1 por empuje y -250 por cada uso del boton deshacer.
 *
 * <p>Las estrellas comparan el puntaje contra el objetivo del nivel:
 * hasta 50% una, hasta 85% dos, y mas de 85% tres.
 */
public class StandardScoreCalculator implements ScoreCalculator {

    private static final int POINTS_PER_STORED_BOX = 100;
    private static final int POINTS_PER_OPENED_DOOR = 100;
    private static final int PENALTY_PER_PUSH = 1;
    private static final int PENALTY_PER_UNDO = 250;
    private static final int ONE_STAR_PERCENT = 50;
    private static final int TWO_STARS_PERCENT = 85;

    @Override
    public ScoreResult calculate(LevelStats stats, int remainingSeconds, int storedBoxes,
                                 int openedDoors, int targetScore) {
        int score = remainingSeconds
                + POINTS_PER_STORED_BOX * storedBoxes
                + POINTS_PER_OPENED_DOOR * openedDoors
                - PENALTY_PER_PUSH * stats.getPushes()
                - PENALTY_PER_UNDO * stats.getUndos();
        return new ScoreResult(score, starsFor(score, targetScore));
    }

    /** Se compara con enteros para no arrastrar errores de redondeo en los limites exactos. */
    private int starsFor(int score, int targetScore) {
        if (targetScore <= 0) {
            return 3;
        }
        if (score * 100 <= ONE_STAR_PERCENT * targetScore) {
            return 1;
        }
        if (score * 100 <= TWO_STARS_PERCENT * targetScore) {
            return 2;
        }
        return 3;
    }
}
