package com.sokoban.port;

public class Progress {
    private final int lastUnlockedLevel;

    public Progress(int lastUnlockedLevel) {
        this.lastUnlockedLevel = lastUnlockedLevel;
    }

    public int getLastUnlockedLevel() {
        return lastUnlockedLevel;
    }
}
