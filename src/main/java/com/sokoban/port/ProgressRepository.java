package com.sokoban.port;

public interface ProgressRepository {

    void save(Progress progress);

    /** Returns the saved progress, or progress at level 1 if nothing was saved yet. */
    Progress load();
}
