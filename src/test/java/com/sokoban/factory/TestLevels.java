package com.sokoban.factory;

import com.sokoban.exception.InvalidLevelException;
import com.sokoban.model.game.Level;

/**
 * Builds levels for tests from map rows.
 *
 * Provisional character legend (adjust it if the team spec says otherwise):
 * <pre>
 *   #  wall              %  void (not accessible)   (space) floor
 *   @  player            $  normal box              1-9 weak box (pushes it withstands)
 *   K  key box           .  storage for normal box  :  storage for weak box
 *   ;  storage for key   ~  ice                     a-e key tile / A-E door of that group
 *   P-S portal (each letter appears exactly twice)
 * </pre>
 */
public final class TestLevels {

    public static final String NAME = "Nivel de prueba";
    public static final int TIME = 120;
    public static final int TARGET = 1000;

    /** Number of header lines before the first map row (3 keys + "---"). */
    public static final int HEADER_LINES = 4;

    private TestLevels() {
    }

    public static String text(String... rows) {
        return "nombre: " + NAME + "\n"
                + "tiempo: " + TIME + "\n"
                + "objetivo: " + TARGET + "\n"
                + "---\n"
                + String.join("\n", rows);
    }

    public static Level level(String... rows) {
        try {
            return new LevelLoader().parse(text(rows));
        } catch (InvalidLevelException e) {
            throw new IllegalArgumentException("Invalid test level: " + e.getMessage(), e);
        }
    }
}
