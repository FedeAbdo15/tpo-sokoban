package com.sokoban.model.game.score;

public class ScoreResult {
    private final int score;
    private final int stars;

    public ScoreResult(int score, int stars) {
        this.score = score;
        this.stars = stars;
    }

    public int getScore() {
        return score;
    }

    public int getStars() {
        return stars;
    }
}
