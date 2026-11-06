package com.sokoban.model.game.score;

import com.sokoban.model.game.LevelStats;

public interface ScoreCalculator {

    ScoreResult calculate(LevelStats stats, int remainingSeconds, int storedBoxes, int openedDoors, int targetScore);
}
