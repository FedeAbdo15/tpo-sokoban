package com.sokoban.model.game;

public class LevelStats {
    private int moves;
    private int pushes;
    private int undos;

    public void addMove() {
        moves++;
    }

    public void addPush() {
        pushes++;
    }

    public void addUndo() {
        undos++;
    }

    public int getMoves() {
        return moves;
    }

    public int getPushes() {
        return pushes;
    }

    public int getUndos() {
        return undos;
    }
}
